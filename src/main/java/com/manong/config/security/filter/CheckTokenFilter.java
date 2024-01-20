package com.manong.config.security.filter;
import com.manong.config.redis.RedisService;
import com.manong.config.security.exception.CustomerAuthenticationException;
import com.manong.config.security.handler.LoginFailureHandler;
import com.manong.config.security.service.CustomerUserDetailsService;
import com.manong.utils.JwtUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Data
@Component
public class CheckTokenFilter extends OncePerRequestFilter{
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private CustomerUserDetailsService customerUserDetailsService;
    @Resource
    private LoginFailureHandler loginFailureHandler;
    @Resource
    private RedisService redisService;
    //获取登录请求地址
    @Value("${request.login.url}")//将配置文件中request.login.url的值注入到 loginUrl 字段中
    private String loginUrl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //测试当前url是否为登录url
            String url = request.getRequestURI();
            //不是登录url就对token验证
            if (!url.equals(loginUrl)) {
                this.validateToken(request);
            }
        }catch (AuthenticationException e){
            loginFailureHandler.onAuthenticationFailure(request,response,e);
        }
        //是登录url就做放行
        doFilter(request,response,filterChain);
    }

    //验证token
    private void validateToken(HttpServletRequest request) throws AuthenticationException{
        //先从请求头中获取token
        String token=request.getHeader("token");
        //如果请求头中没有,那就从请求参数中找(token只可能出现在这两个地方)
        if(ObjectUtils.isEmpty(token)){
            token=request.getParameter("token");
        }
        //如果都没有token,那就抛出异常
        if(ObjectUtils.isEmpty(token)){
            throw new CustomerAuthenticationException("token不存在");
        }
        //判断redis中是否有token信息
        String tokenKey="token_"+token;
        String redisToken=redisService.get(tokenKey);
        //判断redis中是否存在token
        if(ObjectUtils.isEmpty(redisToken)){
            throw new CustomerAuthenticationException("token已过期");
        }
        //判断redis中存着的token和请求的token不一致,抛出异常
        if(!token.equals(redisToken)){
            throw new CustomerAuthenticationException("token验证失败");
        }
        //如果token的确存在,那就解析出用户名
        String username=jwtUtils.getUsernameFromToken(token);
        //判断用户名是否为空
        if(ObjectUtils.isEmpty(username)){
            throw new CustomerAuthenticationException("token解析失败");
        }
        //如果用户名不为空,那就拿到用户信息
        UserDetails userDetails=customerUserDetailsService.loadUserByUsername(username);
        //判断用户信息是否为空
        if(userDetails==null){
            throw new CustomerAuthenticationException("token验证失败");
        }
        //接下来,在验证token成功后,将用户的身份信息存放到 Spring Security 的上下文中
        //(Spring Security 上下文中存储的是认证成功的信息)
        //创建身份验证对象
        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //设置到Spring Security上下文
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}

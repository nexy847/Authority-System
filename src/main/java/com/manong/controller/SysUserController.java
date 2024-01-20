package com.manong.controller;
import com.manong.config.redis.RedisService;
import com.manong.entity.Permission;
import com.manong.entity.User;
import com.manong.entity.UserInfo;
import com.manong.utils.JwtUtils;
import com.manong.utils.MenuTree;
import com.manong.utils.Result;
import com.manong.vo.RouterVo;
import com.manong.vo.TokenVo;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {
    @Resource
    private RedisService redisService;
    @Resource
    private JwtUtils jwtUtils;

    /**
     * 刷新token
     * @param request
     * @return
     */
    @RequestMapping("/refreshToken")
    public Result refreshToken(HttpServletRequest request){
        //先从请求头中获取token
        String token=request.getHeader("token");
        if(ObjectUtils.isEmpty(token)){
            //如果请求头中没有,那就从请求参数中获取
            token=request.getParameter("token");
        }
        //从Spring Security中获取用户信息
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        //获取用户信息
        UserDetails userDetails=(UserDetails) authentication.getPrincipal();
        //定义变量,以保存新的token信息
        String newToken="";
        //验证提交过来的token是否合法
        if(jwtUtils.validateToken(token,userDetails)){
            //重新生成新的token
            newToken= jwtUtils.refreshToken(token);
        }
        //获取本次的token的到期时间
        long expireTime= Jwts.parser()
                .setSigningKey(jwtUtils.getSecret())
                .parseClaimsJws(newToken.replace("jwt_",""))
                .getBody().getExpiration().getTime();
        //清除原来的token信息
        String oldTokenKey="token_"+token;
        redisService.del(oldTokenKey);
        //将新的token信息保存到缓存中
        String newTokenKey="token_"+newToken;
        redisService.set(newTokenKey,newToken, jwtUtils.getExpiration()/1000);
        //创建TokenVo对象
        TokenVo tokenVo=new TokenVo(expireTime,newToken);
        //返回数据
        return Result.ok(tokenVo).message("token刷新成功");
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/getInfo")
    public Result getInfo(){
        //从spring security中获取用户信息
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        //判断authentication对象是否为空
        if(authentication==null){
            return Result.error().message("查询失败");
        }
        //获取用户信息
        User user=(User)authentication.getPrincipal();
        //获取用户权限
        List<Permission> permissionList=user.getPermissionList();
        //获取角色权限编码字段:code
        Object[] roles= permissionList.stream()
                        .filter(Objects::nonNull)
                        .map(Permission::getCode).toArray();
        //创建用户信息对象
        UserInfo userInfo=new UserInfo(user.getId(), user.getNickName(),user.getAvatar(),null,roles);
        //返回数据
        return Result.ok(userInfo).message("用户信息查询成功");
    }

    /**
     * 获取菜单数据
     *
     * @return
     */
    @GetMapping("/getMenuList")
    public Result getMenuList() {
        //从Spring Security上下文获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //获取用户信息
        User user = (User) authentication.getPrincipal();
        //获取相应的权限
        List<Permission> permissionList = user.getPermissionList();
        //筛选目录和菜单,按钮不需要添加到路由菜单中(对于按钮--(增删改查)由userInfo的roles字段控制)
        List<Permission> collect = permissionList.stream()
                .filter(item -> item != null && item.getType() !=2)
                .collect(Collectors.toList());
        //生成路由数据
        List<RouterVo> routerVoList = MenuTree.makeRouter(collect, 0L);
        //返回数据
        return Result.ok(routerVoList).message("菜单数据获取成功");
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request, HttpServletResponse response){
        String token=request.getParameter("token");
        if(ObjectUtils.isEmpty(token)){
            token=request.getHeader("token");
        }
        //获取用户相关信息
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            //清空用户信息
            new SecurityContextLogoutHandler().logout(request,response,authentication);
            //清空redis的token
            String key="token_"+token;
            redisService.del(key);
        }
        return Result.ok().message("用户退出成功");
    }
}

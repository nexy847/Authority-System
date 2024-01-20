package com.manong.config.security;

import com.manong.config.security.filter.CheckTokenFilter;
import com.manong.config.security.handler.AnonymousAuthenticationHandler;
import com.manong.config.security.handler.CustomerAccessDeniedHandler;
import com.manong.config.security.handler.LoginFailureHandler;
import com.manong.config.security.handler.LoginSuccessHandler;
import com.manong.config.security.service.CustomerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@EnableWebSecurity//自动化配置spring security的基本设置,并可重写adapter方法
@Configuration
//开启权限注解控制
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private CustomerUserDetailsService customerUserDetailsService;
    @Resource
    private LoginSuccessHandler loginSuccessHandler;
    @Resource
    private LoginFailureHandler loginFailureHandler;
    @Resource
    private AnonymousAuthenticationHandler anonymousAuthenticationHandler;
    @Resource
    private CustomerAccessDeniedHandler customerAccessDeniedHandler;
    @Resource
    private CheckTokenFilter checkTokenFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //将自定义的checkTokenFilter加到spring security的过滤链中,并加入到username password用户名密码过滤器前
        http.addFilterBefore(checkTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.formLogin()                    //表单登录
                .loginProcessingUrl("/api/user/login")
                .successHandler(loginSuccessHandler)       //认证成功处理器
                .failureHandler(loginFailureHandler)       //认证失败处理器
                .and()
                .csrf().disable()                 //禁用csrf防御机制(使用token验证来替代以保证安全性)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//关闭session(遵循restful api,服务器不保留客户端的任何信息)
                .and()
                .authorizeRequests()                     //设置需要拦截的请求
                .antMatchers("/api/user/login").permitAll()    //登录请求放行(不拦截)
                .anyRequest().authenticated()                  //其他请求一律需要身份验证
                .and()
                .exceptionHandling()//配置异常处理
                .authenticationEntryPoint(anonymousAuthenticationHandler)     //匿名无权限访问
                .accessDeniedHandler(customerAccessDeniedHandler)             //认证用户无权限访问
                .and()
                .cors();        //支持跨域请求
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customerUserDetailsService).passwordEncoder(passwordEncoder());
    }
}

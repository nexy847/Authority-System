package com.manong.config.security.exception;
import org.springframework.security.core.AuthenticationException;
/**
 * 自定义验证异常类
 */
public class CustomerAuthenticationException extends AuthenticationException {
    public CustomerAuthenticationException(String message){
        super(message);
    }
}

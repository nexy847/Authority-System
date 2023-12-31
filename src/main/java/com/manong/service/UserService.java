package com.manong.service;

import com.manong.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lemon
 * @since 2023-11-25
 */
public interface UserService extends IService<User> {

    User findUserByUserName(String username);

}

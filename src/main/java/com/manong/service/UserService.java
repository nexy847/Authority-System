package com.manong.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.manong.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.manong.vo.query.UserQueryVo;

import java.util.List;

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

    IPage<User> findUserListByPage(IPage<User> page, UserQueryVo userQueryVo);

    boolean deleteById(Long id);

    boolean saveUserRole(Long userId,List<Long> roleIds);
}

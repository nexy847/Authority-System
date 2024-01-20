package com.manong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.manong.entity.User;
import com.manong.dao.UserMapper;
import com.manong.service.FileService;
import com.manong.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manong.utils.SystemConstants;
import com.manong.vo.query.UserQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    FileService fileService;

    @Override
    public User findUserByUserName(String username) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<User>();
        queryWrapper.eq("username",username);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     *查询用户列表(分页)
     */
    @Override
    public IPage<User> findUserListByPage(IPage<User> page, UserQueryVo userQueryVo) {
        //创建条件构造器对象
        QueryWrapper<User> queryWrapper=new QueryWrapper<User>();
        //部门编号
        queryWrapper.like(!ObjectUtils.isEmpty(userQueryVo.getDepartmentId()),
                "department_id",userQueryVo.getDepartmentId());
        //用户名
        queryWrapper.like(!ObjectUtils.isEmpty(userQueryVo.getUsername()),
                "username",userQueryVo.getUsername());
        //真实姓名
        queryWrapper.like(!ObjectUtils.isEmpty(userQueryVo.getNickName()),
                "user_name",userQueryVo.getNickName());
        //电话
        queryWrapper.like(!ObjectUtils.isEmpty(userQueryVo.getPhone()),
                "phone",userQueryVo.getPhone());
        //查询并返回
        return baseMapper.selectPage(page,queryWrapper);
    }

    /**
     *根据id删除用户信息
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean deleteById(Long id) {
        //查询
        User user=baseMapper.selectById(id);
        //删除用户与角色的关系
        baseMapper.deleteUserRole(id);
        //删除用户
        if(baseMapper.deleteById(id)>0){
            //所查询的用户不为空且头像也不为空,之后判断头像是否为为默认头像
            if(user!=null && !ObjectUtils.isEmpty(user.getAvatar())
                && !user.getAvatar().equals(SystemConstants.DEFAULT_AVATAR)){
                //如果不是默认头像,则可删除文件
                fileService.deleteFile(user.getAvatar());
            }
            return true;
        }
        return false;
    }

    /**
     *保存用户角色信息
     */
    @Override
    public boolean saveUserRole(Long userId, List<Long> roleIds) {
        //删除用户角色信息
        baseMapper.deleteUserRole(userId);
        //保存用户信息
        return baseMapper.saveUserRole(userId, roleIds)>0;
    }
}

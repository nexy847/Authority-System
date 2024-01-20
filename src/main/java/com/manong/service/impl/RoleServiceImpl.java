package com.manong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.manong.dao.UserMapper;
import com.manong.entity.Role;
import com.manong.dao.RoleMapper;
import com.manong.entity.User;
import com.manong.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manong.vo.query.RoleQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private UserMapper userMapper;

    @Override
    public IPage<Role> findRoleListByUserId(IPage<Role> page, RoleQueryVo roleQueryVo) {
        QueryWrapper<Role> queryWrapper=new QueryWrapper<>();
        //作模糊查询
        queryWrapper.like(!ObjectUtils.isEmpty(roleQueryVo.getRoleName()),"role_name",roleQueryVo.getRoleName());
        queryWrapper.orderByAsc("id");
        User user=userMapper.selectById(roleQueryVo.getUserId());
        if(user!=null&&!ObjectUtils.isEmpty(user.getIsAdmin())&&user.getIsAdmin()!=1){
            //若非admin,则只能查询自己创建的角色
            queryWrapper.eq("create_user",roleQueryVo.getUserId());
        }
        return baseMapper.selectPage(page,queryWrapper);
    }

    /**
     * 更改权限关系
     */
    @Override
    public boolean saveRolePermission(Long roleId, List<Long> permissionIds){
        baseMapper.deleteRolePermission(roleId);
        return baseMapper.saveRolePermission(roleId,permissionIds)>0;
    }

    /**
     *检查权限是否被分配给了用户
     */
    @Override
    public boolean hasRoleCount(Long id) {
        return baseMapper.getRoleCountByRoleId(id)>0;
    }

    @Override
    public boolean deleteRoleById(Long id) {
        baseMapper.deleteRolePermissionByRoleId(id);
        return baseMapper.deleteById(id)>0;
    }

    /**
     *根据用户id查询其角色id
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public List<Long> findRoleIdByUserId(Long userId) {
        return baseMapper.findRoleIdByUserId(userId);
    }

}

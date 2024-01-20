package com.manong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.manong.dao.UserMapper;
import com.manong.entity.Permission;
import com.manong.dao.PermissionMapper;
import com.manong.entity.User;
import com.manong.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.manong.utils.MenuTree;
import com.manong.vo.RolePermissionVo;
import com.manong.vo.query.PermissionQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Resource
    UserMapper userMapper;

    @Override
    public List<Permission> findPermissionListByUserId(Long userId) {
        return baseMapper.findPermissionListByUserId(userId);
    }

    /**
     * 查询菜单列表
     * @param permissionQueryVo
     * @return
     */
    @Override
    public List<Permission> findPermissionList(PermissionQueryVo permissionQueryVo) {
        QueryWrapper<Permission> queryWrapper=new QueryWrapper<Permission>();
        queryWrapper.orderByAsc("order_num");
        List<Permission> permissionList=baseMapper.selectList(queryWrapper);
        List<Permission> permissionTree=MenuTree.makeMenuTree(permissionList,0L);
        return permissionTree;
    }

    /**
     * 查询上级菜单列表
     * @return
     */
    @Override
    public List<Permission> findParentPermission() {
        QueryWrapper<Permission> queryWrapper=new QueryWrapper<Permission>();
        queryWrapper.orderByAsc("order_num");
        List<Permission> permissionList=baseMapper.selectList(queryWrapper);
        Permission permission=new Permission();
        permission.setId(0L);
        permission.setParentId(-1L);
        permission.setLabel("顶级菜单");
        permissionList.add(permission);
        List<Permission> permissionTree=MenuTree.makeMenuTree(permissionList,-1L);
        return permissionTree;
    }

    @Override
    public boolean hasChildrenPermission(Long id) {
        QueryWrapper<Permission> queryWrapper=new QueryWrapper<Permission>();
        queryWrapper.eq("parent_id",id);
        if(baseMapper.selectCount(queryWrapper)>0){
            return true;
        }
        return false;
    }

    @Override
    public RolePermissionVo findPermissionTree(Long userId, Long roleId) {
        User user=userMapper.selectById(userId);
        List<Permission> list=null;
        //判断当前用户角色.如果是管理员就拿到全部权限;如果不是,那就只查询自己拥有的权限
        if(user != null && user.getIsAdmin() != null && user.getIsAdmin() == 1){
            list=baseMapper.selectList(null);
        }else{
            list=baseMapper.findPermissionListByUserId(userId);
        }
        //组装成树数据
        List<Permission> permissionList=MenuTree.makeMenuTree(list,0L);
        //查询要分配角色的原有权限
        List<Permission> rolePermission=baseMapper.findPermissionListByRoleId(roleId);
        //找出当前登录用户的权限和待分配权限角色的权限的交集
        List<Long> listIds=new ArrayList<Long>();
        Optional.ofNullable(list).orElse(new ArrayList<>())
                .stream()
                .filter(obj->obj!=null)//等同于Objects::nonNull
                .forEach(item->{
                    Optional.ofNullable(rolePermission).orElse(new ArrayList<>())
                            .stream()
                            .filter(Objects::nonNull)
                            .forEach(obj->{
                                if(item.getId().equals(obj.getId())){
                                    listIds.add(obj.getId());
                                    return;//forEach的return相当于普通循环的continue
                                }
                            });
                });
        RolePermissionVo rolePermissionVo=new RolePermissionVo();
        rolePermissionVo.setPermissionList(permissionList);//带有树结构
        rolePermissionVo.setCheckList(listIds.toArray());//toArray()可将其转换为Object类型
        return rolePermissionVo;
    }
}

package com.manong.service;

import com.manong.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.manong.vo.RolePermissionVo;
import com.manong.vo.query.PermissionQueryVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lemon
 * @since 2023-11-25
 */
public interface PermissionService extends IService<Permission> {

    List<Permission> findPermissionListByUserId(Long userId);

    //查询菜单列表
    List<Permission> findPermissionList(PermissionQueryVo permissionQueryVo);

    //查询上级权限
    List<Permission> findParentPermission();

    //查询某个菜单下是否有子权限(菜单)
    boolean hasChildrenPermission(Long id);

    //查询分配权限树列表
    RolePermissionVo findPermissionTree(Long userId,Long roleId);
}

package com.manong.vo;

import com.manong.entity.Permission;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RolePermissionVo {
    /**
     * 登录用户的权限菜单数据
     */
    private List<Permission> permissionList=new ArrayList<Permission>();

    /**
     * 等待被分配的角色的原有菜单数据
     */
    private Object [] checkList;
}

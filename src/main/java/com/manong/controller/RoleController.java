package com.manong.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manong.entity.Role;
import com.manong.service.PermissionService;
import com.manong.service.RoleService;
import com.manong.utils.Result;
import com.manong.vo.RolePermissionDTO;
import com.manong.vo.RolePermissionVo;
import com.manong.vo.query.RoleQueryVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Resource
    RoleService roleService;

    @Resource
    PermissionService permissionService;

    /**
     * 查询角色列表
     * @param roleQueryVo
     * @return
     */
    @GetMapping("/list")
    public Result list(RoleQueryVo roleQueryVo){
        IPage<Role> page=new Page<Role>(roleQueryVo.getPageNo(), roleQueryVo.getPageSize());
        roleService.findRoleListByUserId(page,roleQueryVo);
        return Result.ok(page);
    }

    /**
     * 新增角色
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:role:add')")
    public Result add(@RequestBody Role role){//@RequestBody注解用以将json键值对转换为java对象
        if(roleService.save(role)){
            return Result.ok().message("新增角色成功");
        }else{
            return Result.error().message("新增角色失败");
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:edit')")
    public Result update(@RequestBody Role role){
        if(roleService.updateById(role)){
            return Result.ok().message("角色修改成功");
        }else{
            return Result.error().message("角色修改失败");
        }
    }

    @GetMapping("/getAssignPermissionTree")
    @PreAuthorize("hasAuthority('sys:role:assign')")
    public Result getAssignPermissionTree(Long userId,Long roleId){
        RolePermissionVo permissionTree= permissionService.findPermissionTree(userId, roleId);
        return Result.ok(permissionTree);
    }

    @PostMapping("/saveRoleAssign")
    @PreAuthorize("hasAuthority('sys:role:assign')")
    public Result saveRoleAssign(@RequestBody RolePermissionDTO rolePermissionDTO){
        if(roleService.saveRolePermission(rolePermissionDTO.getRoleId(), rolePermissionDTO.getList())){
            return Result.ok().message("权限分配成功");
        }else{
            return Result.error().message("权限分配失败");
        }
    }

    @GetMapping("/check/{id}")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public Result check(@PathVariable Long id){
        if(roleService.hasRoleCount(id)){
            return Result.exist().message("该角色已分配给其他用户使用,无法删除");
        }else{
            return Result.ok();
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public Result delete(@PathVariable Long id){
        if(roleService.deleteRoleById(id)){
            return Result.ok().message("角色删除成功");
        }else{
            return Result.error();
        }
    }
}


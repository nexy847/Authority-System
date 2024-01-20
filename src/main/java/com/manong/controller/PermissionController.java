package com.manong.controller;


import com.manong.entity.Permission;
import com.manong.service.PermissionService;
import com.manong.utils.Result;
import com.manong.vo.query.PermissionQueryVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    @GetMapping("/list")
    public Result list(PermissionQueryVo permissionQueryVo){
        List<Permission> menuList=permissionService.findPermissionList(permissionQueryVo);
        return Result.ok(menuList);
    }

    /**
     * 查询上级菜单列表
     * @return
     */
    @GetMapping("/parent/list")
    public Result getParentPermission(){
        List<Permission> menuList=permissionService.findParentPermission();
        return Result.ok(menuList);
    }

    /**
     * 根据id查询菜单信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getMenuById(@PathVariable Long id){
        return Result.ok(permissionService.getById(id));
    }

    @PostMapping("/add")//post请求用于创建资源
    @PreAuthorize("hasAuthority('sys:menu:add')")
    public Result add(@RequestBody Permission permission){
        if(permissionService.save(permission)){
            return Result.ok().message("菜单添加成功");
        }
        return Result.error().message("菜单添加失败");
    }

    @PutMapping("/update")//put请求用于更新资源
    @PreAuthorize("hasAuthority('sys:menu:edit')")
    public Result update(@RequestBody Permission permission){
        if(permissionService.updateById(permission)){
            return Result.ok().message("菜单修改成功");
        }
        return Result.error().message("菜单修改失败");
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result delete(@PathVariable Long id){
        if(permissionService.removeById(id)){
            return Result.ok().message("删除菜单成功");
        }
        return Result.ok().message("删除菜单失败");
    }

    /**
     * 是否有子菜单,否则无法删除
     * @param id
     * @return
     */
    @GetMapping("/check/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result check(@PathVariable Long id){
        if(permissionService.hasChildrenPermission(id)){
            return Result.exist().message("该菜单下有子菜单,无法删除");
        }
        return Result.ok();
    }
}


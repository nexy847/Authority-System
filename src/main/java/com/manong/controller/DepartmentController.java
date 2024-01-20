package com.manong.controller;


import com.manong.entity.Department;
import com.manong.service.DepartmentService;
import com.manong.utils.Result;
import com.manong.vo.query.DepartmentQueryVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {
    @Resource
    private DepartmentService departmentService;

    /**
     * 查询部门列表
     * @param departmentQueryVo
     * @return
     */
    @GetMapping("/list")
    public Result list(DepartmentQueryVo departmentQueryVo){
        List<Department> departmentList= departmentService.findDepartmentList(departmentQueryVo);
        return Result.ok(departmentList);
    }

    /**
     * 查询上级部门列表
     * @return
     */
    @GetMapping("/parent/list")
    public Result getParentDepartment(){
        List<Department> departmentList=departmentService.findParentDepartment();
        return Result.ok(departmentList);
    }

    /**
     * 添加部门
     * @param department
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:department:add')")
    public Result add(@RequestBody Department department){
        if(departmentService.save(department)){
            return Result.ok().message("部门添加成功");
        }else{
            return Result.error().message("部门添加失败");
        }
    }

    /**
     *修改部门
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('sys:department:edit')")
    public Result update(@RequestBody Department department){
        if(departmentService.updateById(department)){
            return Result.ok().message("部门修改成功");
        }else{
            return Result.error().message("部门修改失败");
        }
    }

    /**
     * 查询某个部门下是否存在子部门
     */
    @GetMapping("/check/{id}")
    @PreAuthorize("hasAuthority('sys:department:delete')")
    public Result check(@PathVariable Long id){
        //调用查询部门下是否存在子部门的方法
        if(departmentService.hasChildrenOfDepartment(id)){
            return Result.exist().message("该部门下存在子部门，无法删除");
        }
        //调用查询部门下是否存在用户的方法
        if(departmentService.hasUserOfDepartment(id)){
            return Result.exist().message("该部门下存在用户，无法删除");
        }
        return Result.ok();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:department:delete')")
    public Result delete(@PathVariable Long id){
        if(departmentService.removeById(id)){
            return Result.ok().message("部门删除成功");
        }else{
            return Result.error().message("部门删除失败");
        }
    }
}


package com.manong.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manong.entity.Role;
import com.manong.entity.User;
import com.manong.service.RoleService;
import com.manong.service.UserService;
import com.manong.utils.Result;
import com.manong.vo.UserRoleDTO;
import com.manong.vo.query.RoleQueryVo;
import com.manong.vo.query.UserQueryVo;
import org.apache.ibatis.annotations.Delete;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@RestController//是@Controller和@ResponseBody注解的结合
@RequestMapping("/api/user")//指定路径
public class UserController {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @RequestMapping("/listAll")
    public Result listAll(){
        return Result.ok(userService.list());
    }

    //查询用户列表
    @GetMapping("/list")
    public Result list(UserQueryVo userQueryVo){
        //创建分页对象
        IPage<User> page=new Page<User>(userQueryVo.getPageNo(), userQueryVo.getPageSize());
        //调用分页查询方法
        userService.findUserListByPage(page,userQueryVo);
        //返回数据
        return Result.ok(page);
    }

    //保存用户
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:user:add')")
    public Result add(@RequestBody User user){
        User item=userService.findUserByUserName(user.getUsername());
        if(item!=null){
            return Result.error().message("该用户名称已被占用,请重新输入");
        }
        //对用户密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //调用保存用户信息的方法
        if (userService.save(user)){
            return Result.ok().message("用户添加成功");
        }
        return Result.error().message("用户添加失败");
    }

    //编辑用户
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('sys:user:edit')")
    public Result update(@RequestBody User user){
        User item=userService.findUserByUserName(user.getUsername());
        if(item!=null&&item.getId()!= user.getId()){
            return Result.error().message("登录名称已被占用");
        }
        if(userService.updateById(user)){
            return Result.ok().message("用户修改成功");
        }else{
            return Result.error().message("用户修改失败");
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result delete(@PathVariable Long id){
        if(userService.deleteById(id)){
            return Result.ok().message("用户删除成功");
        }
        return Result.error().message("用户删除失败");
    }

    @GetMapping("/getRoleListForAssign")//一个新的针对于角色列表的分页查询(新的页数)
    @PreAuthorize("hasAuthority('sys:user:assign')")
    public Result getRoleListForAssign(RoleQueryVo roleQueryVo){
        //创建一个分页对象
        IPage<Role> page=new Page<>(roleQueryVo.getPageNo(), roleQueryVo.getPageSize());
        //调用查询方法
        roleService.findRoleListByUserId(page,roleQueryVo);
        return Result.ok(page);
    }

    @GetMapping("/getRoleByUserId/{userId}")
    @PreAuthorize("hasAuthority('sys:user:assign')")
    public Result getRoleByUserId(@PathVariable Long userId){
        List<Long> roleIds=roleService.findRoleIdByUserId(userId);
        return Result.ok(roleIds);
    }

    @PostMapping("/saveUserRole")
    @PreAuthorize("hasAuthority('sys:user:assign')")
    public Result saveUserRole(@RequestBody UserRoleDTO userRoleDTO){
        if(userService.saveUserRole(userRoleDTO.getUserId(), userRoleDTO.getRoleIds())){
            return Result.ok().message("角色分配成功");
        }else{
            return Result.error().message("角色分配失败");
        }
    }
}


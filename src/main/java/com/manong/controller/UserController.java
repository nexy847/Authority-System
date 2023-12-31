package com.manong.controller;


import com.manong.service.UserService;
import com.manong.utils.Result;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService service;

    @RequestMapping("/listAll")
    public Result listAll(){
        return Result.ok(service.list());
    }
}


package com.manong.dao;

import com.manong.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Delete("delete from sys_user_role where user_id=#{userId}")
    int deleteUserRole(@Param("userId") Long userId);

    int saveUserRole(Long userId,List<Long> roleIds);
}

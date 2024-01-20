package com.manong.dao;

import com.manong.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lemon
 * @since 2023-11-25
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 删除一个角色原有的权限关系
     */
    @Delete("delete from sys_role_permission where role_id = #{roleId}")
    void deleteRolePermission(Long roleId);

    /**
     *保存权限关系(循环插入)
     */
    int saveRolePermission(Long roleId, List<Long> permissionIds);

    /**
     * 检查角色是否分配给了用户
     */
    @Select("select count(1) from sys_user_role where role_id=#{roleId}")
    int getRoleCountByRoleId(@Param("roleId") Long id);

    /**
     * 删除权限关系
     */
    @Delete("delete from sys_role_permission where role_id=#{roleId}")
    void deleteRolePermissionByRoleId(@Param("roleId") Long roleId);

    @Select("select role_id from sys_user_role where user_id=#{userId}")
    List<Long> findRoleIdByUserId(@Param("userId") Long userId);

}

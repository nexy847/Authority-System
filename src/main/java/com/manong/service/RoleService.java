package com.manong.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.manong.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.manong.vo.query.RoleQueryVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lemon
 * @since 2023-11-25
 */
public interface RoleService extends IService<Role> {

    IPage<Role> findRoleListByUserId(IPage<Role> page, RoleQueryVo roleQueryVo);

    boolean saveRolePermission(Long roleId, List<Long> permissionIds);

    boolean hasRoleCount(Long id);

    boolean deleteRoleById(Long id);

    List<Long> findRoleIdByUserId(Long userId);
}

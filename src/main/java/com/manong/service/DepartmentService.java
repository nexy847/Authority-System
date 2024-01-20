package com.manong.service;

import com.manong.entity.Department;
import com.baomidou.mybatisplus.extension.service.IService;
import com.manong.vo.query.DepartmentQueryVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lemon
 * @since 2023-11-25
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 查询部门列表
     */
    List<Department> findDepartmentList(DepartmentQueryVo departmentQueryVo);

    /**
    *  查询上级部门列表
     */
    List<Department> findParentDepartment();

    /**
     * 判断部门下是否有子列表
     * @param id
     * @return
     */
    boolean hasChildrenOfDepartment(Long id);

    /**
     * 判断部门下是否有用户
     * @param id
     * @return
     */
    boolean hasUserOfDepartment(Long id);

}

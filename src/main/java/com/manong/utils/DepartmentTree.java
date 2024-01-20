package com.manong.utils;

import com.manong.entity.Department;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentTree {

    public static List<Department> makeDepartmentTree(List<Department> deptList,Long pid){
        //创建集合保存部门信息
        List<Department> list=new ArrayList<Department>();
        //如果deptList列表不为空,则使用部门列表,否则创建集合对象
        Optional.ofNullable(deptList).orElse(new ArrayList<>())
                .stream().filter(item->item!=null && item.getPid() == pid)
                .forEach(item->{
                    Department dept = new Department();
                    //复制部门属性给item
                    BeanUtils.copyProperties(item,dept);
                    //获取每个item的下级部门,递归生成部门树
                    List<Department> children = makeDepartmentTree(deptList, item.getId());
                    //设置子部门
                    dept.setChildren(children);
                    //加入到部门树中
                    list.add(dept);
                });
        return list;
    }
}

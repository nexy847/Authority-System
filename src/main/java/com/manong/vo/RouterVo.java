package com.manong.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouterVo {//该类需要和前端vue-element-admin的动态路由数据格式一致
    //路由地址
    private String path;
    //路由对应的组件
    private String component;
    //是否显示
    private boolean alwaysShow;
    //路由名称
    private String name;
    //路由meta信息
    private Meta meta;

    @Data
    @AllArgsConstructor
    public class Meta{
        private String title;
        private String icon;
        private Object[] roles;
    }

    private List<RouterVo> children=new ArrayList<RouterVo>();
}

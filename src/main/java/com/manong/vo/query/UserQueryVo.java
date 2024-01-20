package com.manong.vo.query;

import com.manong.entity.User;
import lombok.Data;

@Data
public class UserQueryVo extends User {
    private Long pageNo=1L;
    private Long pageSize=10L;
}

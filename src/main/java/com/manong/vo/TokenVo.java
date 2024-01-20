package com.manong.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
//前端需要拿到的token
public class TokenVo {
    //过期时间
    private Long expireTime;
    //token
    private String token;
}

package com.lifepulse.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;
    private String phone;
    private String nickname;
    private Integer status;
    private String avatar;
    private LocalDateTime createTime;
    private String token;
}
package com.lifepulse.dto;

import lombok.Data;

@Data
public class LoginByCodeDTO {
    private String phone;
    private String code;
}

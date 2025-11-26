package com.microtech.smartshop.dto;

import com.microtech.smartshop.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDTO {


    private Long id;
    private String username;
    private UserRole role; // ADMIN ou CLIENT

}

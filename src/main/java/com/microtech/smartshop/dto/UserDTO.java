package com.microtech.smartshop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private Long id;
    private String username;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private UserRole role; // ADMIN ou CLIENT

}

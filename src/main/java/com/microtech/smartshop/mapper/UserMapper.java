package com.microtech.smartshop.mapper;

import com.microtech.smartshop.dto.UserDTO;
import com.microtech.smartshop.entity.User;

import org.mapstruct.Mapper;



@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);
    User toEntity(UserDTO dto);


}


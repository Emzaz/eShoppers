package com.emzaz.eshoppers.service;

import com.emzaz.eshoppers.dtos.LoginDto;
import com.emzaz.eshoppers.dtos.UserDto;

import java.nio.file.attribute.UserPrincipalNotFoundException;

public interface UserService {
    void saveUser(UserDto userDto);

    boolean isNotUniqueUsername(UserDto user);
    boolean isNotUniqueEmail(UserDto user);
    boolean isNotUniqueFirstName(UserDto user);
    boolean isNotUniqueLastName(UserDto user);

    UserDto verifyUser(LoginDto loginDto) throws UserPrincipalNotFoundException;
}

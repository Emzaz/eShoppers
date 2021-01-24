package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.dtos.UserDto;

import java.util.Optional;

public interface UserRepository {
    void save(UserDto userDto);

    Optional<UserDto> findByUserName(String username);
    Optional<UserDto> findByEmail(String email);
    Optional<UserDto> findByFirstName(String firstName);
    Optional<UserDto> findByLastName(String lastName);
}

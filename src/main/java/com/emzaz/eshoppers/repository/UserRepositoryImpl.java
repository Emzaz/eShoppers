package com.emzaz.eshoppers.repository;

import com.emzaz.eshoppers.dtos.UserDto;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class UserRepositoryImpl implements UserRepository {
    private static final Set<UserDto> USERS = new CopyOnWriteArraySet<>();

    @Override
    public void save(UserDto userDto) {
        USERS.add(userDto);
    }

    @Override
    public Optional<UserDto> findByUserName(String username) {
        for(UserDto user : USERS) {
            if(Objects.equals(user.getUsername(), username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        for(UserDto user : USERS) {
            if(Objects.equals(user.getEmail(), email)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserDto> findByFirstName(String firstName) {
        for(UserDto user : USERS) {
            if(Objects.equals(user.getFirstName(), firstName)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserDto> findByLastName(String lastName) {
        for(UserDto user : USERS) {
            if(Objects.equals(user.getLastName(), lastName)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}

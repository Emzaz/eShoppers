package com.emzaz.eshoppers.service;

import com.emzaz.eshoppers.dtos.LoginDto;
import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(UserDto userDto) {
        String encrypted = encryptPassword(userDto.getPassword());
        UserDto user = new UserDto();

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(encrypted);
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        userRepository.save(user);
    }

    private String encryptPassword(String password) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to encrypt password", e);
        }
    }
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for(byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public boolean isNotUniqueUsername(UserDto user) {
        return userRepository.findByUserName(user.getUsername()).isPresent();
    }

    @Override
    public boolean isNotUniqueEmail(UserDto user) {
        return userRepository.findByEmail(user.getEmail()).isPresent();
    }

    @Override
    public boolean isNotUniqueFirstName(UserDto user) {
        return userRepository.findByFirstName(user.getFirstName()).isPresent();
    }

    @Override
    public boolean isNotUniqueLastName(UserDto user) {
        return userRepository.findByLastName(user.getLastName()).isPresent();
    }

    @Override
    public UserDto verifyUser(LoginDto loginDto) throws UserPrincipalNotFoundException {
        UserDto userDto = userRepository.findByUserName(loginDto.getUsername()).orElseThrow(() ->
                new UserPrincipalNotFoundException("User not found by " +loginDto.getUsername()));

        String encrypted = encryptPassword(loginDto.getPassword());
        if(userDto.getPassword().equals(encrypted)) {
            return userDto;
        } else {
            throw new UserPrincipalNotFoundException("Incorrect username password.");
        }
    }
}

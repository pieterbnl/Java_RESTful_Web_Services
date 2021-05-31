package com.appsdeveloperblog.app.ws.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import com.appsdeveloperblog.app.ws.shared.dto.UserDTO;

import java.util.List;

public interface UserService extends UserDetailsService {
    // Note: all below methods need to implemented under UserServiceImpl.java,
    // because USerServiceImpl.java implements this UserService interface
    UserDTO createUser(UserDTO user);
    UserDTO getUser(String email);
    UserDTO getUserByUserId(String userId);
    UserDTO updateUser(String userId, UserDTO user);
    void deleteUser(String userId);
    List<UserDTO> getUsers(int page, int limit);
}
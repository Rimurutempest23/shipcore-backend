package com.shipcore.user.service;

import com.shipcore.user.dto.request.UserRequest;
import com.shipcore.user.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);

}
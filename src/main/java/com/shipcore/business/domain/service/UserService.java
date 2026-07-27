package com.shipcore.business.domain.service;

import com.shipcore.business.api.dto.request.UserRequest;
import com.shipcore.business.api.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse update(Long id, UserRequest request);

    void delete(Long id);

}

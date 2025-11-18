package com.planprostructure.planpro.service.users;

import java.util.List;

import com.planprostructure.planpro.payload.users.UserResponseDTO;
import com.planprostructure.planpro.payload.users.UpdateProfileRequest;

public interface UserService {
    Object getProfile() throws Throwable;

    void updateProfile(UpdateProfileRequest paylod) throws Throwable;

    List<UserResponseDTO> getAllUsersContacts() throws Throwable;
}

package com.planprostructure.planpro.service.users;

import com.planprostructure.planpro.components.common.api.StatusCode;
//import com.planprostructure.planpro.domain.proTalk.UserChat;
//import com.planprostructure.planpro.domain.proTalk.UserChatRepository;
import com.planprostructure.planpro.domain.users.UserRepository;
import com.planprostructure.planpro.domain.users.Users;
import com.planprostructure.planpro.enums.AuthProvider;
import com.planprostructure.planpro.exception.BusinessException;
import com.planprostructure.planpro.helper.AuthHelper;
import com.planprostructure.planpro.payload.users.UpdateProfileRequest;
import com.planprostructure.planpro.payload.users.UserProfileResponse;
import com.planprostructure.planpro.payload.users.UserResponseDTO;
import com.planprostructure.planpro.properties.FileInfoConfig;
import com.planprostructure.planpro.utils.ImageUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileInfoConfig fileInfoConfig;
    // private final UserChatRepository userChatRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile() {

        // 1️⃣ Get current user-id directly from the static AuthHelper
        Long userId = AuthHelper.getUserId();

        // 2️⃣ Load the user from the DB
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        Boolean isPass = user.getPassword() != null ? true : false;
        System.out.println("isPass: " + isPass);
        // 3️⃣ Map entity → DTO
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dob(user.getDateOfBirth())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .role(user.getRole().name())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .profileImageUrl(user.getProfileImageUrl())
                .authProvider(user.getAuthProvider().name())
                .isPass(isPass)
                .build();
    }

    @Override
    @Transactional
    public void updateProfile(UpdateProfileRequest payload) {
        Long userId = AuthHelper.getUserId();

        // Find the user first to verify it exists
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        if (user == null) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }
        if (user.getAuthProvider() == AuthProvider.GOOGLE) {
            throw new BusinessException(StatusCode.GOOGLE_USERS_CANNOT_UPDATE_EMAIL,
                    "Google users cannot update their email");
        }
        // Execute the update query
        // userRepository.updateProfile(
        // payload.getUsername(),
        // payload.getEmail(),
        // payload.getFirstName(),
        // payload.getLastName(),
        // payload.getPhoneNumber(),
        // payload.getDob(),
        // payload.getImageUrl(),
        // userId
        // );
        user.setUsername(payload.getUsername());
        user.setEmail(payload.getEmail());
        user.setFirstName(payload.getFirstName());
        user.setLastName(payload.getLastName());
        user.setPhoneNumber(payload.getPhoneNumber());
        user.setDateOfBirth(payload.getDob());
        user.setProfileImageUrl(ImageUtil.getImageUrl(fileInfoConfig.getBaseUrl(), payload.getImageUrl()));

        userRepository.save(user);

    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsersContacts() {
        Long userId = AuthHelper.getUserId();
        return userRepository.findAllUsersContacts(userId).stream().map(user -> UserResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .profileImageUrl(user.getProfileImageUrl())
                .build()).collect(Collectors.toList());
    }
}

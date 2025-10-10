package com.planprostructure.planpro.service.auth;

import com.planprostructure.planpro.components.common.api.StatusCode;
import com.planprostructure.planpro.domain.users.UserRepository;
import com.planprostructure.planpro.domain.users.Users;
import com.planprostructure.planpro.enums.AuthProvider;
import com.planprostructure.planpro.enums.Role;
import com.planprostructure.planpro.enums.StatusUser;
import com.planprostructure.planpro.exception.BusinessException;
import com.planprostructure.planpro.payload.auth.GoogleOAuth2UserInfo;
import com.planprostructure.planpro.payload.auth.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(registrationId, oauth2User.getAttributes());

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new BusinessException(StatusCode.BAD_REQUEST, "Email not found from OAuth2 provider");
        }

        Optional<Users> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        Users user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user if provider doesn't match
            if (!user.getAuthProvider().equals(AuthProvider.valueOf(registrationId.toUpperCase()))) {
                throw new BusinessException(StatusCode.BAD_REQUEST,
                        "Looks like you're signed up with " + user.getAuthProvider() + " account. Please use your " +
                                user.getAuthProvider() + " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userRequest, oAuth2UserInfo);
        }

        return new OAuth2UserPrincipal(user, oauth2User.getAttributes());
    }

    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, java.util.Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else {
            throw new RuntimeException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }

    private Users registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Users user = new Users();
        user.setAuthProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
        user.setEmail(oAuth2UserInfo.getEmail());

        // Split name into first and last name
        String name = oAuth2UserInfo.getName();
        if (oAuth2UserInfo instanceof GoogleOAuth2UserInfo googleUserInfo) {
            user.setFirstName(googleUserInfo.getGivenName());
            user.setLastName(googleUserInfo.getFamilyName());
        } else if (name != null) {
            String[] nameParts = name.split(" ", 2);
            user.setFirstName(nameParts[0]);
            user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        }

        user.setUsername(oAuth2UserInfo.getEmail());
        user.setProfileImageUrl(oAuth2UserInfo.getImageUrl());
        user.setRole(Role.USER);
        user.setStatus(StatusUser.ACTIVE);
        user.setPassword(null); // OAuth users don't have passwords

        return userRepository.save(user);
    }

    private Users updateExistingUser(Users existingUser, OAuth2UserInfo oAuth2UserInfo) {
        if (oAuth2UserInfo.getImageUrl() != null) {
            existingUser.setProfileImageUrl(oAuth2UserInfo.getImageUrl());
        }
        return userRepository.save(existingUser);
    }
}
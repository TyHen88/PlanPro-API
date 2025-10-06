package com.planprostructure.planpro.payload.users;

import org.springframework.beans.factory.annotation.Value;

public interface GetIUserContacts {
    @Value("#{target.id}")
    Long getId();

    @Value("#{target.first_name}")
    String getFirstName();

    @Value("#{target.last_name}")
    String getLastName();

    @Value("#{target.username}")
    String getUsername();

    @Value("#{target.phone_number}")
    String getPhoneNumber();

    @Value("#{target.role}")
    String getRole();

    @Value("#{target.profile_image_url}")
    String getProfileImageUrl();
}

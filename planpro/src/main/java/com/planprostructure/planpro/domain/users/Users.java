package com.planprostructure.planpro.domain.users;

import com.planprostructure.planpro.domain.UpdatableEntity;
import com.planprostructure.planpro.enums.Role;
import com.planprostructure.planpro.enums.StatusUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone"),
        @UniqueConstraint(columnNames = "username")
})
@Builder
public class Users extends UpdatableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assuming the ID is auto-generated
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(unique = true)
    private Long telegramId;

    @Column(name = "usr_dob")
    private String dateOfBirth;

    @Column(name = "usr_fn")
    private String firstName;

    @Column(name = "usr_ln")
    private String lastName;

    @Column(name = "pwd")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role; // e.g., ADMIN, USER, etc.

    @Column(name = "phone")
    private String phoneNumber;

    @Column(name = "sts",nullable = false, length = Types.CHAR)
    @JdbcTypeCode(Types.CHAR)
    @Convert(converter = StatusUser.Converter.class)
    private StatusUser status;


    @Column(name = "gender")
    private String gender;

    @Column(name = "profile_image_url")
    private String profileImageUrl;



}

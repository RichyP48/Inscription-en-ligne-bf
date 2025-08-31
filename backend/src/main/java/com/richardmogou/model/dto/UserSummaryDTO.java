package com.richardmogou.model.dto;

import com.richardmogou.model.entity.ApplicationStatus;
import com.richardmogou.model.entity.AuthProvider;
import com.richardmogou.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> roles; // Represent roles as strings
    private AuthProvider provider;
    private Boolean enabled;
    private Boolean locked;
    private LocalDateTime createdAt;
    private ApplicationStatus applicationStatus; // Add application status field

    // Static factory method for easy conversion from User entity
    public static UserSummaryDTO fromUser(com.richardmogou.model.entity.User user) {
        if (user == null) {
            return null;
        }
        return UserSummaryDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream().map(Role::name).collect(Collectors.toSet()))
                .provider(user.getProvider())
                .enabled(user.isEnabled())
                .locked(user.isAccountNonLocked() ? false : true) // Map isAccountNonLocked back to locked
                .createdAt(user.getCreatedAt())
                .applicationStatus(user.getApplicationStatus()) // Include application status
                .build();
    }
}
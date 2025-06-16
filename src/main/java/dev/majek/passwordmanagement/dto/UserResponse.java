package dev.majek.passwordmanagement.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String fullName;
    private String email;

    public UserResponse(String id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }
}

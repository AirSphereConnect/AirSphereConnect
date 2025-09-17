package com.airSphereConnect.dtos.request;

public class UserRequestDto {
    private String username;
    private String email;

    public UserRequestDto() {
    }

    public UserRequestDto(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}

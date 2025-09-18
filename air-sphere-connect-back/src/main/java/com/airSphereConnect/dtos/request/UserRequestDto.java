package com.airSphereConnect.dtos.request;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestDto {

    @NotBlank(message = "{user.username.required}")
    @Size(min = 1, message = "{user.username.min}")
    @Size(max = 50, message = "{user.username.max}")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    @NotBlank(message = "{user.password.required}")
    @Size(min = 8, message = "{user.password.min}")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "{user.address.required}")
    @Valid
    private AddressRequestDto address;


    public UserRequestDto() {
    }

    public UserRequestDto(String username, String email, String password, AddressRequestDto address) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.address = address;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AddressRequestDto getAddress() {
        return address;
    }

    public void setAddress(AddressRequestDto address) {
        this.address = address;
    }
}

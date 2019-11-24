package com.ita.if103java.ims.dto;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Component
public class UserLoginDto implements Serializable {

    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public UserLoginDto() {
    }

    public UserLoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
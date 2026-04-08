package com.xius.TariffBuilder.Entity;

import lombok.Data;

@Data
public class LoginForm {

    private String role; // USER or ADMIN
    private String networkName; // Only for USER
    private String username;
    private String password;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
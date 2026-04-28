package com.example.aitrainer.dto;

public class LoginRequest {
    // Field named "username" but accepts username OR email — backend handles the lookup
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

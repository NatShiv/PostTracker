package com.example.posttracker.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewPasswordDto implements Serializable {
    private String currentPassword;
    private String newPassword;
}
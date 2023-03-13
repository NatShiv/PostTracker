package com.example.posttracker.model.dto;

import com.example.posttracker.constant.RoleEnum;
import lombok.Data;
import java.io.Serializable;

/**
 * A DTO for the entity
 */
@Data
public class RegisterDto implements Serializable {
    private final String id;
    private final String name;
    private int postOfficeIndex;
    private String password;
    private RoleEnum roleEnum;
}
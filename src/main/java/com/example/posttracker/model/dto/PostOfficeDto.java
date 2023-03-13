package com.example.posttracker.model.dto;

import lombok.Data;

/**
 * A DTO for the {@link com.example.posttracker.model.entity.PostOffice} entity
 */
@Data
public class PostOfficeDto {
    private int index;
    private String title;
    private String address;
    private String phone;
}

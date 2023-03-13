package com.example.posttracker.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link com.example.posttracker.model.entity.Postman} entity
 */
@Data
public class PostmanDto implements Serializable {
    private final String id;
    private final String name;
    private int postOfficeIndex;
 }
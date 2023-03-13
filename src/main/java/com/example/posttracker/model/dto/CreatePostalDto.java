package com.example.posttracker.model.dto;

import com.example.posttracker.constant.Type;
import lombok.Data;

@Data
public class CreatePostalDto {
    private final Type type;
    private final long recipientPhone;
    private final String recipientName;
    private final String recipientAddress;

}

package com.example.posttracker.model.dto;

import com.example.posttracker.constant.Status;
import com.example.posttracker.constant.Type;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link com.example.posttracker.model.entity.PostalItem} entity
 */
@Data
public class PostalItemFullDto implements Serializable {
    private final String tracker;
    private final Type type;
    private final Status status;
    private final List<PostOfficeDto> fullRoute;
     private final long recipientPhone;
    private final String recipientName;
    private final String recipientAddress;
    private final String lastPostOfficeIndex;

}
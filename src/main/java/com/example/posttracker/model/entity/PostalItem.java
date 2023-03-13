package com.example.posttracker.model.entity;

import com.example.posttracker.constant.Status;
import com.example.posttracker.constant.Type;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.util.Stack;

@Entity
@Data
@NoArgsConstructor
public class PostalItem {
    @Id
    @NonNull
    private String tracker;
    @NonNull
    private Type type;
    @NonNull
    private Status status;
    @ManyToOne
    @ToString.Exclude
    private Recipient recipient;

    @ElementCollection
    private Stack<Integer> route;
    @ManyToOne
    @ToString.Exclude
    private PostOffice lastPostOffice;

}

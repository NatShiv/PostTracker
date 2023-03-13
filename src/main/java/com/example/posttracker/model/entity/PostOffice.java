package com.example.posttracker.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class PostOffice {
    @Id
    @Size(min=6, message = "Не меньше 6 знаков")
    private int index;
    private String title;
    private String address;
    private String phone;

    @OneToMany(mappedBy = "postOffice", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Postman> postmans;

    @OneToMany(mappedBy = "lastPostOffice", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PostalItem> postalItems;
}

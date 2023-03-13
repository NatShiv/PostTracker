package com.example.posttracker.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Recipient {
    @Id
    @Size(min=10, max=10, message= " введите 10 цифр номера телефона без первой цифры и специальных символов")
    private long phone;
    @NonNull
    private String name;
    @NonNull
    private String address;
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PostalItem> postalItems;

}

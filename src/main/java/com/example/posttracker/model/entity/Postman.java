package com.example.posttracker.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
public class Postman {
    @Id
    @Size(min=7,message = "номер должен содержать минимум 7 символов")
    @NonNull
    private String id;
    @NonNull
    private String name;
    @ManyToOne
    @ToString.Exclude
    @NonNull
       private PostOffice postOffice;

}

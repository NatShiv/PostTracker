package com.example.posttracker.model.repository;

import com.example.posttracker.model.entity.Postman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostmanRepository extends JpaRepository<Postman, String> {
}
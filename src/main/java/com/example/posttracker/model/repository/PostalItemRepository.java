package com.example.posttracker.model.repository;

import com.example.posttracker.model.entity.PostalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostalItemRepository extends JpaRepository<PostalItem, String> {
}
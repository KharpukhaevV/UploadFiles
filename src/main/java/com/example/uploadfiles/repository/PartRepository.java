package com.example.uploadfiles.repository;

import com.example.uploadfiles.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Integer> {
    Part findByPath (String path);
}

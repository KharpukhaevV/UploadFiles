package com.example.uploadfiles.repository;

import com.example.uploadfiles.model.Drawing;
import com.example.uploadfiles.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrawingRepository extends JpaRepository<Drawing, Integer> {
    Drawing findByNameOriginalAndPart(String name, Part part);

    void deleteById(Long id);
}

package com.example.uploadfiles.repository;

import com.example.uploadfiles.model.Drawing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrawingRepository extends JpaRepository<Drawing, Integer> {
}

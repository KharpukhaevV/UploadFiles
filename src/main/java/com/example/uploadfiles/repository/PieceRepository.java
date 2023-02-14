package com.example.uploadfiles.repository;

import com.example.uploadfiles.model.Piece;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PieceRepository extends JpaRepository<Piece, Integer> {
}

package com.example.uploadfiles.repository;

import com.example.uploadfiles.model.Drawing;
import com.example.uploadfiles.model.Piece;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PieceRepository extends JpaRepository<Piece, Integer> {
    Piece findByNameAndDrawing(String name, Drawing drawing);
    List<Piece> findAllByDrawing(Drawing drawing);
}

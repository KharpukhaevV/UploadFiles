package com.example.uploadfiles.repository;

import com.example.uploadfiles.model.DirectoryTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectoryTreeRepository extends JpaRepository<DirectoryTree, Integer> {
    DirectoryTree findById(Long id);
}

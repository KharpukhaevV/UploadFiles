package com.example.uploadfiles.repository;

import com.example.uploadfiles.model.FileDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDetailsRepository extends JpaRepository<FileDetails, Integer> {

}

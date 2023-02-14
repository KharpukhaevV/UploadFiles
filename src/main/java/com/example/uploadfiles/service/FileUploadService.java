package com.example.uploadfiles.service;


import com.example.uploadfiles.model.FileDetails;
import com.example.uploadfiles.payload.FileUploadResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface FileUploadService {

  public FileUploadResponse uploadFile(MultipartFile file, String path) throws IOException;

  public Resource fetchFileAsResource(String fileName) throws FileNotFoundException;

  public List<FileDetails> getAllFiles();
}

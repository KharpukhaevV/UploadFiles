package com.example.uploadfiles.controller;

import com.example.uploadfiles.exception.FileNotSupportedException;
import com.example.uploadfiles.model.FileDetails;
import com.example.uploadfiles.payload.FileUploadResponse;
import com.example.uploadfiles.service.FileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "file")
@CrossOrigin(origins = "http://localhost:3000")
public class FileUploadController {

  @Autowired
  private FileUploadService fileUploadService;

  @GetMapping
  @ResponseStatus(code = HttpStatus.OK)
  public List<FileDetails> getAllFiles() {
    return this.fileUploadService.getAllFiles();
  }

  @PostMapping(value = "/upload")
  public ResponseEntity<Object> uploadFiles(@RequestParam("files") MultipartFile[] files) {

    try {
      List<FileUploadResponse> fileUploadResponses =
          Arrays.stream(files).map(file -> {
            try {
              return fileUploadService.uploadFile(file);
            } catch (IOException e) {
              throw new UncheckedIOException(e);
            }
          }).collect(Collectors.toList());

      return new ResponseEntity<>(fileUploadResponses, HttpStatus.OK);
    } catch (UncheckedIOException e) {
      return new ResponseEntity<>(e.getMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (FileNotSupportedException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/download/{fileName:.+}")
  public ResponseEntity<Object> downloadFile(@PathVariable String fileName,
                                             HttpServletRequest request) {

    try {
      Resource resource = this.fileUploadService.fetchFileAsResource(fileName);
      String contentType =
          request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

      if (contentType == null) {
        contentType = "application/octet-stream";
      }

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + resource.getFilename() + "\"")
          .body(resource);
    } catch (IOException ex) {
      return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
  }
}

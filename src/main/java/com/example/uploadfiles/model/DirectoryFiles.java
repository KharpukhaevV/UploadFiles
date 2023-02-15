package com.example.uploadfiles.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class DirectoryFiles {
    private String name;
    private List<MultipartFile> files;

    public DirectoryFiles() {}

    public DirectoryFiles(String name, List<MultipartFile> files) {
        this.name = name;
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }
}
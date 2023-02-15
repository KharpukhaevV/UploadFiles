package com.example.uploadfiles.controller;

import com.example.uploadfiles.model.DirectoryTree;
import com.example.uploadfiles.repository.DirectoryTreeRepository;
import com.example.uploadfiles.service.FileUploadService;
import com.example.uploadfiles.service.ProcessingPart;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

@RestController
@RequestMapping(value = "file")
@CrossOrigin(origins = "http://localhost:3000")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private DirectoryTreeRepository directoryTreeRepository;
    @Autowired
    private ProcessingPart processingPart;

    @GetMapping(value = "/upload")
    @ResponseStatus(code = HttpStatus.OK)
    public JSONObject getTree() throws ParseException, FileNotFoundException {
        DirectoryTree directoryTree = directoryTreeRepository.findById(1L);
        JSONObject json;
        JSONParser parser = new JSONParser();
        if (directoryTree != null) {
            json = (JSONObject) parser.parse(directoryTree.getJsonInString());
        } else {
            json = (JSONObject) parser.parse(new FileReader("/home/vladimir/IdeaProjects/UploadFiles/frontend/src/static/catalog.json"));
        }
        return json;
    }


    @PostMapping(value = "/upload")
    public String uploadFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("path") String path, @RequestParam("dirName") String dirName) {
        String message = "Файлы успешно загружены.";
        System.out.println(dirName);
        try {
            String dirPath = "";
            boolean flagDrawing = false;
            boolean flagSpecification = false;
            try {
                for (MultipartFile file : files) {
                    if (file.getOriginalFilename().toLowerCase().contains("jpg") ||
                            file.getOriginalFilename().toLowerCase().contains("pdf")) {
                        flagDrawing = true;
                        dirPath = fileUploadService.uploadFile(file, path, dirName);
                    } else if (file.getOriginalFilename().toLowerCase().contains("xls") ||
                            file.getOriginalFilename().toLowerCase().contains("xlsx") ||
                            file.getOriginalFilename().toLowerCase().contains("ods")) {
                        flagSpecification = true;
                        dirPath = fileUploadService.uploadFile(file, path, dirName);
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            processingPart.process(dirPath);

            if (!flagDrawing) {
                message = message + "\nНехватает чертежа.";
            }
            if (!flagSpecification) {
                message = message + "\nНехватает спецификации.";
            }


            return message;

        } catch (UncheckedIOException e) {
            message = e.getMessage();
            return message;
        }
    }
}

package com.example.uploadfiles.controller;

import com.example.uploadfiles.exception.FileNotSupportedException;
import com.example.uploadfiles.model.DirectoryTree;
import com.example.uploadfiles.model.FileDetails;
import com.example.uploadfiles.model.Part;
import com.example.uploadfiles.payload.FileUploadResponse;
import com.example.uploadfiles.repository.DirectoryTreeRepository;
import com.example.uploadfiles.repository.PartRepository;
import com.example.uploadfiles.service.FileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
    @Autowired
    private DirectoryTreeRepository directoryTreeRepository;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<FileDetails> getAllFiles() {
        return this.fileUploadService.getAllFiles();
    }

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
    public String uploadFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("path") String path) {
        String message = "Файлы успешно загружены.";
        try {
            System.out.println(path);
            boolean flagDrawing = false;
            boolean flagSpecification = false;
            for (MultipartFile file : files) {
                if (file.getOriginalFilename().toLowerCase().contains("jpg") ||
                        file.getOriginalFilename().toLowerCase().contains("pdf")) {
                    flagDrawing = true;
                }
                if (file.getOriginalFilename().toLowerCase().contains("xls") ||
                        file.getOriginalFilename().toLowerCase().contains("xlsx") ||
                        file.getOriginalFilename().toLowerCase().contains("ods")) {
                    flagSpecification = true;
                }
                try {
                    //TODO: Изменить порядок, сначало сохраняю и только потом в отдельном методе смотрю в папку и сохраняю в бд
                    fileUploadService.uploadFile(file, path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            if (!flagDrawing) {
                message = message + "\nНехватает чертежа.";
            }
            if (!flagSpecification) {
                message = message + "\nНехватает спецификации.";
            }


            return message;

        } catch (UncheckedIOException | FileNotSupportedException e) {
            message = e.getMessage();
            return message;
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

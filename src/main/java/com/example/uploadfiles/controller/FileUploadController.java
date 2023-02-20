package com.example.uploadfiles.controller;

import com.example.uploadfiles.model.DirectoryTree;
import com.example.uploadfiles.model.Drawing;
import com.example.uploadfiles.model.Part;
import com.example.uploadfiles.model.Piece;
import com.example.uploadfiles.repository.DirectoryTreeRepository;
import com.example.uploadfiles.repository.DrawingRepository;
import com.example.uploadfiles.repository.PartRepository;
import com.example.uploadfiles.repository.PieceRepository;
import com.example.uploadfiles.service.DirectoryStructureToJson;
import com.example.uploadfiles.service.FileUploadService;
import com.example.uploadfiles.service.ProcessingPart;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping(value = "file")
@CrossOrigin(origins = "http://localhost:3000")
public class FileUploadController {
    @Autowired
    private DirectoryStructureToJson directoryStructureToJson;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private DirectoryTreeRepository directoryTreeRepository;
    @Autowired
    private PartRepository partRepository;
    @Autowired
    private ProcessingPart processingPart;
    @Autowired
    private DrawingRepository drawingRepository;
    @Autowired
    private PieceRepository pieceRepository;

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

    @PostMapping(value = "/create_dir")
    public void createDir(@RequestParam("dirName") String dirName, @RequestParam("path") String path) throws IOException {
        Path newPath = Paths.get(path + "/" + dirName);
        if (!Files.exists(newPath)) {
            Files.createDirectories(newPath);
        }
        directoryStructureToJson.updateTree();
    }

    @PostMapping(value = "/delete_dir")
    public void deleteDir(@RequestParam("dirName") String dirName, @RequestParam("path") String path) {
        Path newPath = Paths.get(path + "/" + dirName);
        File file = new File(newPath.toUri());
        if (Files.exists(newPath)) {
            if (fileUploadService.deleteDirectory(file)) {
                Part part = partRepository.findByPath(file.getAbsolutePath());
                if (part != null) {
                    partRepository.delete(part);
                }
                directoryStructureToJson.updateTree();
            }
        }
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

            directoryStructureToJson.updateTree();

            return message;

        } catch (UncheckedIOException e) {
            message = e.getMessage();
            return message;
        }
    }
}

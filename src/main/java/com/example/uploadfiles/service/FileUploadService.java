package com.example.uploadfiles.service;

import com.example.uploadfiles.repository.DrawingRepository;
import com.example.uploadfiles.repository.PartRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class FileUploadService {
    public FileUploadService() {
    }


    public String uploadFile(MultipartFile file, String serverPath, String dirName) throws IOException {
        Path UPLOAD_PATH = Paths.get(serverPath + "/" + dirName);
        String partPath = serverPath + "/" + dirName;
        if (!Files.exists(UPLOAD_PATH)) {
            Files.createDirectories(UPLOAD_PATH);
        }

        String fileName;
        String[] filePaths = file.getOriginalFilename().split("/");
        String pathStr = UPLOAD_PATH.toString();
        for (int i = 0; i < filePaths.length - 1; i++) {
            pathStr += "/" + filePaths[i];
        }
        Path path = Paths.get(pathStr);
        fileName = filePaths[filePaths.length - 1];

        Files.createDirectories(path);
        Path filePath = path.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, REPLACE_EXISTING);
        if (fileName.toLowerCase().contains("pdf")) {
            Files.copy(file.getInputStream(), filePath, REPLACE_EXISTING);
            convertPDFtoJPG(filePath.toString(), filePath.getParent().toString());
            Files.delete(filePath);
        }
        return partPath;
    }

    public void convertPDFtoJPG(String pdfPath, String rootPath) {
        try {

            File sourceFile = new File(pdfPath);
            File destinationFile = new File(rootPath);

            if (!destinationFile.exists()) {
                destinationFile.mkdir();
                System.out.println("Folder Created -> " + destinationFile.getAbsolutePath());
            }

            if (sourceFile.exists()) {
                PDDocument document = PDDocument.load(sourceFile);
                PDFRenderer pdfRenderer = new PDFRenderer(document);

                String fileName = sourceFile.getName().replace(".pdf", "");

                for (int pageNumber = 0; pageNumber < document.getNumberOfPages(); ++pageNumber) {
                    BufferedImage bim = pdfRenderer.renderImage(pageNumber);

                    String destDir = rootPath + "/" + fileName + "_" + pageNumber + ".jpg";

                    ImageIO.write(bim, "jpg", new File(destDir));
                }

                document.close();

                System.out.println("Image saved at -> " + destinationFile.getAbsolutePath());
            } else {
                System.err.println(sourceFile.getName() + " File does not exist");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean deleteDirectory(File directoryToBeDeleted) {
        System.out.println(directoryToBeDeleted);
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}


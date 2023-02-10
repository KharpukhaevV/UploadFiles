package com.example.uploadfiles.service;

import com.example.uploadfiles.model.FileDetails;
import com.example.uploadfiles.payload.FileUploadResponse;
import com.example.uploadfiles.repository.FileDetailsRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileUploadSerivceImpl implements FileUploadService {

    private FileDetailsRepository fileDetailsRepository;

    public FileUploadSerivceImpl(FileDetailsRepository fileDetailsRepository) throws IOException {
        this.fileDetailsRepository = fileDetailsRepository;
    }


    private final Path UPLOAD_PATH =
            Paths.get("/home/vladimir/IdeaProjects/UploadFiles/src/main/resources/static");

    @Override
    public FileUploadResponse uploadFile(MultipartFile file) throws IOException {
        if (!Files.exists(UPLOAD_PATH)) {
            Files.createDirectories(UPLOAD_PATH);
        }

        // file format validation
//    if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
//      throw new FileNotSupportedException("only .jpeg and .png images are " + "supported");
//    }
        System.out.println(UPLOAD_PATH);
        String fileName = "";
        if (file.getOriginalFilename().contains("/")) {
            String[] filePaths = file.getOriginalFilename().split("/");
            String pathStr = UPLOAD_PATH.toString();
            for (int i = 0; i < filePaths.length - 1; i++) {
                pathStr += "/" + filePaths[i];
            }
            Path path = Paths.get(pathStr);
            fileName = filePaths[filePaths.length - 1];


            if (fileName.toLowerCase().contains("pdf") ||
                    fileName.toLowerCase().contains("jpeg") ||
                    fileName.toLowerCase().contains("xls") ||
                    fileName.toLowerCase().contains("xlsx") ||
                    fileName.toLowerCase().contains("ods")) {
                Files.createDirectories(path);
                Path filePath = path.resolve(fileName);
//                Files.copy(file.getInputStream(), filePath);
                if (fileName.toLowerCase().contains("pdf")){
                    convertPDFtoJPG(filePath.toString(), filePath.getParent().toString());
                }
            }


        } else {
            fileName = file.getOriginalFilename();
            Path filePath = UPLOAD_PATH.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
        }


        String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/image/").path(fileName).toUriString();

        String fileDownloadUri =
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/file/download/").path(fileName).toUriString();

        FileDetails fileDetails = new FileDetails(file.getOriginalFilename(),
                fileUri,
                fileDownloadUri, file.getSize());

        this.fileDetailsRepository.save(fileDetails);

        FileUploadResponse fileUploadResponse =
                new FileUploadResponse(fileDetails.getId(),
                        file.getOriginalFilename(), fileUri, fileDownloadUri,
                        file.getSize());

        return fileUploadResponse;
    }

    @Override
    public Resource fetchFileAsResource(String fileName) throws FileNotFoundException {

        try {
            Path filePath = UPLOAD_PATH.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName);
        }
    }

    @Override
    public List<FileDetails> getAllFiles() {
        return this.fileDetailsRepository.findAll();
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

                // int pageNumber = 0;

                // for (PDPage page : document.getPages()) {
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
}


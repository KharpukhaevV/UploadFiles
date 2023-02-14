package com.example.uploadfiles.service;

import com.example.uploadfiles.model.Drawing;
import com.example.uploadfiles.model.FileDetails;
import com.example.uploadfiles.model.Part;
import com.example.uploadfiles.payload.FileUploadResponse;
import com.example.uploadfiles.repository.DrawingRepository;
import com.example.uploadfiles.repository.FileDetailsRepository;
import com.example.uploadfiles.repository.PartRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class FileUploadSerivceImpl implements FileUploadService {
    @Autowired
    private PartRepository partRepository;
    @Autowired
    private DrawingRepository drawingRepository;
    @Autowired
    private DirectoryStructureToJson directoryStructureToJson;
    private final FileDetailsRepository fileDetailsRepository;

    public FileUploadSerivceImpl(FileDetailsRepository fileDetailsRepository) throws IOException {
        this.fileDetailsRepository = fileDetailsRepository;
    }


    @Override
    public FileUploadResponse uploadFile(MultipartFile file, String serverPath) throws IOException {
        Path UPLOAD_PATH = Paths.get(serverPath);
        if (!Files.exists(UPLOAD_PATH)) {
            Files.createDirectories(UPLOAD_PATH);
        }

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
                    fileName.toLowerCase().contains("jpg") ||
                    fileName.toLowerCase().contains("xls") ||
                    fileName.toLowerCase().contains("xlsx") ||
                    fileName.toLowerCase().contains("ods")) {
                Files.createDirectories(path);
                Path filePath = path.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, REPLACE_EXISTING);
                if (fileName.toLowerCase().contains("pdf")) {
                    Files.copy(file.getInputStream(), filePath, REPLACE_EXISTING);
                    convertPDFtoJPG(filePath.toString(), filePath.getParent().toString());
                    Files.delete(filePath);
                }
            }

        } else {
            fileName = file.getOriginalFilename();
            Path filePath = UPLOAD_PATH.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
        }

        directoryStructureToJson.updateTree();
        Drawing drawing = new Drawing();
        drawing.setNameOriginal(fileName);
        String[] split = file.getOriginalFilename().split("/");
        String namePhysical = "";
        for (int i = 1; i < split.length; i++) {
            namePhysical += "/" + split[i];
        }
        drawing.setNamePhysical(namePhysical);
        //TODO: Добавить спецификацию и примечание
        Part part = partRepository.findByPath(serverPath.split("catalogdata")[1] + "/" + file.getOriginalFilename().split("/")[0]);
        if (part == null) {
            Part newPart = new Part();
            newPart.setPath(serverPath.split("catalogdata")[1] + "/" + file.getOriginalFilename().split("/")[0]);
            partRepository.save(newPart);
            drawing.setPart(newPart);
        } else {
            drawing.setPart(part);
        }
        //TODO: Сохраняются с неправильным разширением
        drawingRepository.save(drawing);

        //TODO: Парсить xls и добавлять в Piece

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

            Path UPLOAD_PATH = Paths.get("/home/vladimir/IdeaProjects/catalogdata");
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


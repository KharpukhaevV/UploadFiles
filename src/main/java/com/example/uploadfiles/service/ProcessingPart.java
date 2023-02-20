package com.example.uploadfiles.service;

import com.example.uploadfiles.model.Drawing;
import com.example.uploadfiles.model.Part;
import com.example.uploadfiles.model.Piece;
import com.example.uploadfiles.repository.DrawingRepository;
import com.example.uploadfiles.repository.PartRepository;
import com.example.uploadfiles.repository.PieceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ProcessingPart {
    @Autowired
    private PartRepository partRepository;
    @Autowired
    private DrawingRepository drawingRepository;
    @Autowired
    private PiecesReader piecesReader;
    @Autowired
    private PieceRepository pieceRepository;

    public void process(String partPath) {
        if (Files.exists(Path.of(partPath))) {
            Part part = updatePart(partPath);
            File dir = new File(partPath);
            File[] arrFiles = dir.listFiles();
            if (arrFiles != null) {
                List<File> files = Arrays.asList(arrFiles);
                updateDrawing(files, part);
            }
        }

    }

    public Part updatePart(String path) {
        Part part = partRepository.findByPath(path);
        if (part == null) {
            Part newPart = new Part();
            newPart.setPath(path);
            partRepository.save(newPart);
            return newPart;
        } else {
            return part;
        }
    }

    public void updateDrawing(List<File> files, Part part) {
        String specFileName = "";
        for (File file : files) {
            if (file.getName().endsWith(".xlsx") || file.getName().endsWith("xls") || file.getName().endsWith("ods")) {
                specFileName = file.getName();
            }
        }

        for (File file : files) {
            if (!file.isDirectory()) {
                Drawing drawing = drawingRepository.findByNameOriginalAndPart(file.getName(), part);
                drawingRepository.save(setDrawingFields(Objects.requireNonNullElseGet(drawing, Drawing::new), file, specFileName, part));
            }
        }
    }

    private Drawing setDrawingFields(Drawing drawing, File file, String specFileName, Part part) {
        drawing.setNameOriginal(file.getName());
        drawing.setNamePhysical(file.getName());
        drawing.setPart(part);
        if (file.getName().endsWith(".jpg")) {
            drawing.setSpecfile(specFileName);
        } else if (file.getName().endsWith("docx")) {    //Возможо не только .docx
            drawing.setTextData(piecesReader.readTextData(file.getAbsolutePath()));
        } else if (file.getName().endsWith(".xlsx") || file.getName().endsWith("xls") || file.getName().endsWith("ods")) {
            for (Piece piece : piecesReader.readExcelPiecesFile(file.getPath())) {
                piece.setDrawing(drawing);
                pieceRepository.save(piece);
            }
        }
        return drawing;
    }
}

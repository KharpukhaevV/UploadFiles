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

@Service
public class ProcessingPart {
    @Autowired
    private DirectoryStructureToJson directoryStructureToJson;
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

                directoryStructureToJson.updateTree();
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

    @Transactional
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
                if (drawing != null) {
                    drawingRepository.deleteById(drawing.getId());
                }
                Drawing newDrawing = new Drawing();
                newDrawing.setNameOriginal(file.getName());
                //TODO: Проверять вложенные папки
                newDrawing.setNamePhysical(file.getName());
                newDrawing.setPart(part);
                if (file.getName().endsWith(".jpg")) {
                    newDrawing.setSpecfile(specFileName);
                } else if (file.getName().endsWith("docx")) {    //Возможо не только .docx
                    newDrawing.setTextData(piecesReader.readTextData(file.getAbsolutePath()));
                } else if (file.getName().endsWith(".xlsx") || file.getName().endsWith("xls") || file.getName().endsWith("ods")) {
                    for (Piece piece : piecesReader.readExcelPiecesFile(file.getPath())) {
                        piece.setDrawing(newDrawing);
                        pieceRepository.save(piece);
                    }
                }
                drawingRepository.save(newDrawing);
            }
        }
    }
}

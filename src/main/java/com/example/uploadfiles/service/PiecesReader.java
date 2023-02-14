package com.example.uploadfiles.service;

import com.example.uploadfiles.model.Drawing;
import com.example.uploadfiles.model.Piece;
import com.example.uploadfiles.repository.PieceRepository;
import com.example.uploadfiles.service.utils.OdsReader;
import com.example.uploadfiles.service.utils.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class PiecesReader {
    @Autowired
    public PieceRepository pieceRepository;

    public void readPieces(List<Drawing> drawings, String drawingsPath) {
        HashMap<String, ArrayList<Piece>> map = new HashMap<>();
        List<Piece> pieces = new ArrayList<>();
        for (Drawing d : drawings){
            String specfileFullPath = drawingsPath + d.getPart().getPath() + File.separator + d.getSpecfile();
            if (!Objects.equals("", d.getSpecfile())){
                if (!map.containsKey(specfileFullPath)) {
                    ArrayList<Piece> excelPieces = readExcelPiecesFile(specfileFullPath);
                    if (excelPieces != null) {
                        map.put(specfileFullPath, excelPieces);
                    }
                }
                ArrayList<Piece> excelPieces = map.get(specfileFullPath);
                if (excelPieces!=null) {
                    for (Piece xlp : excelPieces) {
                        Piece pce = new Piece();
                        pce.setDrawing(d);
                        pce.setCode(xlp.getCode());
                        pce.setComment(xlp.getComment());
                        pce.setCount(xlp.getCount());
                        pce.setName(xlp.getName());
                        pce.setNumber(xlp.getNumber());
                        pieces.add(pce);
                    }
                }
            }
        }
        pieceRepository.saveAll(pieces);
    }

    public ArrayList<Piece> readExcelPiecesFile(String path) {
        ArrayList<Piece> result = null;
        try {
            if (path.endsWith(".xlsx")) {
                Reader readerXl = new Reader();
                result = readerXl.readPiecesFile(path);
            } else if (path.endsWith(".ods")) {
                OdsReader readerOds = new OdsReader();
                result = readerOds.readPiecesFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}

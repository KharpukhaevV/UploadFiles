package com.example.uploadfiles.service.utils;


import com.example.uploadfiles.model.Piece;
import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OdsReader {


    public ArrayList<Piece> readPiecesFile(String path) throws IOException {
        boolean debug = false;
        ArrayList<Piece> xl_pieces = new ArrayList<>();
        SpreadSheet spread = new SpreadSheet(new File(path));

        List<Sheet> sheets = spread.getSheets();
        Sheet sheet = sheets.size() > 0 ? sheets.get(0) : null;
        if (sheet != null) {
            String currentPosition = "";
            Piece xlp = null;
            int rows = sheet.getMaxRows();

            for (int i=0; i<=rows-1; i++) {
                Range position  = sheet.getRange(i,0);
                Range code = sheet.getRange(i,1);
                Range name = sheet.getRange(i,2);
                Range count = sheet.getRange(i,3);
                Range comment = sheet.getRange(i,4);

                String s1 = getStringValueOrEmpty(position);
                String s2 = getStringValueOrEmpty(code);
                String s3 = getStringValueOrEmpty(name);
                String s4 = getStringValueOrEmpty(count);
                String s5 = getStringValueOrEmpty(comment);


                if (true) {
                    if (s1.matches(".*[0-9]+.*")) {
                        currentPosition = s1;
                        //System.out.println("-----------------------------------------------------------------------");
                        //System.out.println(path);
                        if (xlp != null) {
                            xl_pieces.add(xlp);
                        }
                        int intCount = 0;
                        if (!Objects.equals("", s4)) {
                            try {
                                intCount = (int) Double.parseDouble(s4);
                            } catch (Exception e) {
                                System.out.println(String.format("PG_ERROR Ошибка преобразования строки в вещественное число: [%s] [%s] строка#%s",s4, path, i+1));
                            }
                        }
                        xlp = new Piece();
                        xlp.setNumber(s1);
                        xlp.setCode(s2);
                        xlp.setName(s3);
                        xlp.setCount(intCount);
                        xlp.setComment("");
                    } else {

                    }
                    if (Objects.equals(currentPosition, s1)) {

                    } else {

                    }
                    if (xlp != null) {
                        String nl = "";
                        if (!Objects.equals("", xlp.getComment())) {
                            nl = "\n";
                        }
                        xlp.setComment(xlp.getComment() + nl + s5);
                    }
                    if (debug) System.out.println(String.format("%s | %s | %s | %s | %s", s1, s2, s3, s4, s5));
                }
                i++;
            }
            xl_pieces.add(xlp);
            if (debug) {
                for (Piece xl : xl_pieces) {
                    System.out.println("------------------");
                    System.out.println(xl.getNumber());
                    System.out.println(xl.getCode());
                    System.out.println(xl.getName());
                    System.out.println(xl.getCount());
                    System.out.println(xl.getComment());
                    System.out.println("------------------");
                }
            }
        }
        return xl_pieces;
    }

    private String getStringValueOrEmpty(Range cell) {
        Object c = cell.getValues()[0];
        if (((Object[]) c).length > 0) {
            if (((Object[]) c)[0] != null) {
                return ((Object[]) c)[0].toString();
            }
        }
        return "";
    }
}

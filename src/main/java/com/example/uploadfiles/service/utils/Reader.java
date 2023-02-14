package com.example.uploadfiles.service.utils;


import com.example.uploadfiles.model.Piece;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Reader {


    private boolean addToList(ArrayList<String> list, Cell part) {
        String strPart = getStringValueOrEmpty(part);
        if (strPart!=""){
            if (!list.contains(strPart)){
                list.add(strPart);
                return true;
            }
        }
        return false;
    }

    public static int getIntOrZero(String s){
        try{
            return Integer.parseInt(s);
        } catch (Exception ex){
            ex.printStackTrace();
            return 0;
        }
    }

    public static int getIntOrZero(Cell c) {
        int result = 0;
        switch (c.getCellType()) {
            case STRING:  result = getIntOrZero(c.getStringCellValue()); break;
            case NUMERIC: result = (int) c.getNumericCellValue(); break;
            case BOOLEAN: result=0; break;
            case FORMULA: result=0; break;
            default: result = 0;
        }
        return result;
    }


    public static String getStringValueOrEmpty(Cell c) {
        String result = "";
        if (c!=null) {
            switch (c.getCellType()) {
                case STRING:
                    result = c.getStringCellValue();
                    break;
                case NUMERIC:
//                    double d = c.getNumericCellValue();
//                    if (d % 1 == 0) {
//                        result = String.format("%.0f",c.getNumericCellValue());
//                    } else {
//                        result = String.format("%f", c.getNumericCellValue());
//                    }
                    result = NumberToTextConverter.toText(c.getNumericCellValue());
                    break;
                case BOOLEAN:
                    result = String.format("%s", c.getNumericCellValue());
                    break;
                case FORMULA:
                    result = String.format("%s", c.getStringCellValue());
                    break;
            }
        }
        return result;
    }

    public ArrayList<Piece> readPiecesFile(String path) throws IOException {
        boolean debug = false;
        ArrayList<Piece> xl_pieces = new ArrayList<>();
        FileInputStream file = new FileInputStream(new File(path));
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook(file);
        } catch (NotOfficeXmlFileException e){
            System.out.println(String.format("PG_ERROR Неверный формат файла: %s", path));
            return null;
        }
        Sheet sheet = workbook.getSheetAt(0);
        int i = 0;
        String currentPosition = "";
        Piece xlp = null;
        for (Row row : sheet) {
                i++;
                Cell position = row.getCell(0);
                Cell code = row.getCell(1);
                Cell name = row.getCell(2);
                Cell count = row.getCell(3);
                Cell comment = row.getCell(4);

                String s1 = getStringValueOrEmpty(position);
                String s2 = getStringValueOrEmpty(code);
                String s3 = getStringValueOrEmpty(name);
                String s4 = getStringValueOrEmpty(count);
                String s5 = getStringValueOrEmpty(comment);



                if (true){
                    if (s1.matches(".*[0-9]+.*")){
                        currentPosition = s1;
                        //System.out.println("-----------------------------------------------------------------------");
                        //System.out.println(path);
                        if (xlp != null) {
                            xl_pieces.add(xlp);
                        }
                        int intCount = 0;
                        if (!Objects.equals("",s4)){
                            try {
                                intCount = (int) Double.parseDouble(s4);
                            } catch (Exception e){
                                //e.printStackTrace();
                                System.out.println(String.format("PG_ERROR Ошибка преобразования строки в вещественное число: [%s] [%s] строка#%s",s4, path, i));
                            }
                        }
                        if (!(s1.length()>0 && s2.length()==0 && s3.length()==0 && intCount==0 && s5.length()==0)) {
                            xlp = new Piece();
                            xlp.setNumber(s1);
                            xlp.setCode(s2);
                            xlp.setName(s3);
                            xlp.setCount(intCount);
                            xlp.setComment("");
                        } else {
                            System.out.println("PG_ERROR UNABLE_TO_READ_ROW Не удалось прочитать строку спецификации: " + s1 + " " + path);
                        }
                    } else {

                    }
                    if (Objects.equals(currentPosition, s1)) {

                    } else {

                    }
                    if (xlp!=null) {
                        String nl = "";
                        if (!Objects.equals("",xlp.getComment())){
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
        return xl_pieces;
    }


    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private String getDateValue(Cell date) {
        //if (date.getCellType() == CellType.NUMERIC) System.out.println (date.getNumericCellValue());

        if (date.getCellType() != CellType.STRING) {
            if (DateUtil.isCellDateFormatted(date)) {
                if (date.getDateCellValue()!=null) {
                    //System.out.println(date.getDateCellValue());
                    return sdf.format(date.getDateCellValue());
                }
            }
        }
        return "";
    }
}
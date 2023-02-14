package com.example.uploadfiles.service.utils;


import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;

public class DocxReader {

    public static String readSimpleDocx(String path){
        String result = "";
        try {
            FileInputStream fis = new FileInputStream(path);
            XWPFDocument xdoc = new XWPFDocument(OPCPackage.open(fis));
            XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
            result = (extractor.getText());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}

package com.example.uploadfiles;

import com.example.uploadfiles.service.ProcessingPart;

public class Main {
    public static void main(String[] args) {
        ProcessingPart processingPart = new ProcessingPart();
        processingPart.process("/home/vladimir/IdeaProjects/catalogdata/Т1М/Электросхемы");
    }
}

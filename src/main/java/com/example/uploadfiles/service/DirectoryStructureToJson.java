package com.example.uploadfiles.service;


import com.example.uploadfiles.model.DirectoryTree;
import com.example.uploadfiles.model.Node;
import com.example.uploadfiles.repository.DirectoryTreeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Directory Structure to json
 */
@Service
public class DirectoryStructureToJson {
    @Autowired
    private DirectoryTreeRepository directoryTreeRepository;

    public void updateTree() {
        try {
            Node node = getNode(new File("/home/vladimir/IdeaProjects/catalogdata"));
            String jsonInString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(node);

            String path = "/home/vladimir/IdeaProjects/UploadFiles/frontend/src/static/catalog.json";

            PrintWriter out = new PrintWriter(new FileWriter(path));
            out.write(jsonInString);
            out.flush();
            out.close();
            DirectoryTree directoryTree = directoryTreeRepository.findById(1L);
            if (directoryTree == null) {
                DirectoryTree newDirectoryTree = new DirectoryTree();
                newDirectoryTree.setJsonInString(jsonInString);
                directoryTreeRepository.save(newDirectoryTree);
            } else {
                directoryTree.setJsonInString(jsonInString);
                directoryTreeRepository.save(directoryTree);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static Node getNode(File node) {
        if (node.isDirectory()) {
            return new Node(node.getName(), node.getAbsolutePath(), "folder", getDirList(node));
        } else {
            return new Node(node.getName(), node.getAbsolutePath(), "file");
        }
    }


    public static List<Node> getDirList(File node) {
        List<Node> nodeList = new ArrayList<>();
        for (File n : node.listFiles()) {
            nodeList.add(getNode(n));
        }
        return nodeList;
    }
}
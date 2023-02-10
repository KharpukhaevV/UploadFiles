package com.example.uploadfiles;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Directory Structure to json
 */
public class DirectoryStructureToJson {

    public static void main(String args[]) {
        try {
            String jsonInString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(
                    getNode(new File("/home/vladimir/IdeaProjects/catalogdata"))
            );

            String path = "/home/vladimir/IdeaProjects/UploadFiles/frontend/src/static/catalog.json";

            PrintWriter out = new PrintWriter(new FileWriter(path));
            out.write(jsonInString);
            out.flush();
            out.close();
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

    public static class Node {
        private String name;
        private String location;
        private String type;
        private List<Node> items;

        public Node() {
        }

        public Node(String name, String location, String type, List<Node> items) {
            this.name = name;
            this.location = location;
            this.type = type;
            this.items = items;
        }

        public Node(String name, String location, String type) {
            this.name = name;
            this.location = location;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Node> getItems() {
            return items;
        }

        public void setItems(List<Node> items) {
            this.items = items;
        }
    }


}
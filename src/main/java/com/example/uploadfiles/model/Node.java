package com.example.uploadfiles.model;


import java.util.List;

public class Node {
    private String name;
    private String location;
    private String type;
    private List<Node> items;


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

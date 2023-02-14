package com.example.uploadfiles.model;


import jakarta.persistence.*;

@Entity
public class DirectoryTree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 3000000)
    private String jsonInString;

    public DirectoryTree() {
    }

    public String getJsonInString() {
        return jsonInString;
    }

    public void setJsonInString(String jsonInString) {
        this.jsonInString = jsonInString;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

package com.example.uploadfiles.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Part {
    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private int exportVersion;
    private String model; // Т1М | Т2 | Т3
    private String code;
    private String name;
    private String path;
    private String groupName;
    private int sortNumber;

    @OneToMany(mappedBy="part", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference(value = "drawing-part")
    private Set<Drawing> drawings = new HashSet<>();

    public Part() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getExportVersion() {
        return exportVersion;
    }

    public void setExportVersion(int exportVersion) {
        this.exportVersion = exportVersion;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
    }

    public Set<Drawing> getDrawings() {
        return drawings;
    }

    public void setDrawings(Set<Drawing> drawings) {
        this.drawings = drawings;
    }

    @Override
    public String toString(){
        return String.format("v.%s | Model: %s | Code: %s | Group: %s | Name: %s | SortNumber: %d | Path : %s", exportVersion, model, code, groupName, name, sortNumber, path);
    }
}

package com.example.uploadfiles.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Drawing {
    private @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private String namePhysical;
    private String nameOriginal;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "part_id")
    @JsonBackReference(value = "drawing-part")
    private Part part;
    @OneToMany(mappedBy = "drawing", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference(value = "piece-drawing")
    private Set<Piece> pieces = new HashSet<>();
    private String specfile;
    @Column(columnDefinition = "TEXT")
    private String textData;

    public Drawing() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamePhysical() {
        return namePhysical;
    }

    public void setNamePhysical(String namePhysical) {
        this.namePhysical = namePhysical;
    }

    public String getNameOriginal() {
        return nameOriginal;
    }

    public void setNameOriginal(String nameOriginal) {
        this.nameOriginal = nameOriginal;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Set<Piece> getPieces() {
        return pieces;
    }

    public void setPieces(Set<Piece> pieces) {
        this.pieces = pieces;
    }

    public String logId() {
        return id == null ? "" : String.format(" id=%s", id);
    }


    public String getSpecfile() {
        return specfile;
    }

    public void setSpecfile(String specfile) {
        this.specfile = specfile;
    }

    public String getTextData() {
        return textData;
    }

    public void setTextData(String textData) {
        this.textData = textData;
    }
}

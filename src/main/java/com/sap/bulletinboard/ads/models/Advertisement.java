package com.sap.bulletinboard.ads.models;

import org.hibernate.validator.constraints.NotBlank;
import javax.persistence.*;

@Entity
@Table(name = "advertisements")
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(name = "mytitle")
    private String title;

    public Advertisement() {
    }

    public Advertisement(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
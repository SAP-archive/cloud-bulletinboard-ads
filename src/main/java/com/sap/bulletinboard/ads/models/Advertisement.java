package com.sap.bulletinboard.ads.models;

import org.hibernate.validator.constraints.NotBlank;

public class Advertisement {

    @NotBlank
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
}
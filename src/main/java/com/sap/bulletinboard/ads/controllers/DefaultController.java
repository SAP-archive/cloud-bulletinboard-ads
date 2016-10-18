package com.sap.bulletinboard.ads.controllers;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class DefaultController {

    @Inject
    ServletContext context;

    @GetMapping
    public String get() {

        return "ok";
    }
}
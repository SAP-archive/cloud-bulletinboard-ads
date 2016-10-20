package com.sap.bulletinboard.ads.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class DefaultController {

    @GetMapping
    public String get() {

        return "ok";
    }
    
    @GetMapping("/instance-index")
    public String getIndex(@Value("${CF_INSTANCE_INDEX}") String instanceIndex) {
        return "Instance index: " + instanceIndex;
    }
}
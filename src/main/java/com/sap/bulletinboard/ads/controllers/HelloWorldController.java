package com.sap.bulletinboard.ads.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("hello")
@RestController
public class HelloWorldController {

    @GetMapping(path = "/{name}") // same like @RequestMapping(method = RequestMethod.GET)
    public String responseMsg(@PathVariable("name") String name) {
        return "Welcome: " + name;
    }
}
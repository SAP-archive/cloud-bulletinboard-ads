package com.sap.bulletinboard.statistics.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class DefaultResource {
    @GET
    public String get() {
        return "Statistics: OK";
    }
}

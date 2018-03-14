package com.sap.bulletinboard.statistics.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import com.sap.bulletinboard.statistics.models.Statistics;
import com.sap.bulletinboard.statistics.util.StatisticsCounter;

@Path("api/v1/statistics")
@Produces(MediaType.APPLICATION_JSON)
public class StatisticsResource {
    @Inject
    private Logger logger;

    @Inject
    StatisticsCounter statisticsCounter;

    @PUT
    @Path("{id}")
    public Statistics put(@PathParam("id") long id) {
        logger.info("put for ID: {}", id);
        return statisticsCounter.increment(id);
    }

    @GET
    @Path("{id}")
    public Statistics get(@PathParam("id") long id) {
        logger.info("get for ID: {}", id);
        return statisticsCounter.get(id);
    }
}

package com.sap.bulletinboard.ads.controllers;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.bulletinboard.ads.config.WebAppContextConfig;
import com.sap.bulletinboard.ads.models.Advertisement;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { WebAppContextConfig.class })
@WebAppConfiguration
//@formatter:off
public class AdvertisementControllerTest {
    
    private static final String LOCATION = "Location";
    private static final String SOME_TITLE = "MyNewAdvertisement";
    private static final String SOME_OTHER_TITLE = "MyOldAdvertisement";

    @Inject
    WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void create() throws Exception {
        mockMvc.perform(buildPostRequest(SOME_TITLE))
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, is(not(""))))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.title", is(SOME_TITLE))); // requires com.jayway.jsonpath:json-path
    }

    @Test
    public void createAndGetByLocation() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(buildPostRequest(SOME_TITLE))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        // check that the returned location is correct
        mockMvc.perform(get(response.getHeader(LOCATION)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(SOME_TITLE)));
    }
    
    @Test
    public void readAll() throws Exception {
        mockMvc.perform(buildPostRequest(SOME_TITLE))
            .andExpect(status().isCreated());

        mockMvc.perform(buildGetRequest(""))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.length()", is(both(greaterThan(0)).and(lessThan(10)))));
    }

    @Test
    public void readByIdNotFound() throws Exception {
        mockMvc.perform(buildGetRequest("4711"))
            .andExpect(status().isNotFound());
    }
    
    @Test
    public void readByIdNegative() throws Exception {
        mockMvc.perform(buildGetRequest("-1"))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    public void createEmptyTitle() throws Exception {
        mockMvc.perform(buildPostRequest(null))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    public void createBlancTitle() throws Exception {
        mockMvc.perform(buildPostRequest(""))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    public void createWithNoContent() throws Exception {
        mockMvc.perform(post(AdvertisementController.PATH).contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());
    }
    
    @Test
    public void readById() throws Exception {
        String id = performPostAndGetId();

        mockMvc.perform(buildGetRequest(id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.title", is(SOME_TITLE)));
    }
    
    @Test
    public void updateNotFound() throws Exception {
        Advertisement advertisement = new Advertisement(SOME_TITLE);

        mockMvc.perform(buildPutRequest("4711", advertisement)).andExpect(status().isNotFound());
    }

    @Test
    public void updateById() throws Exception {
        
        MockHttpServletResponse response = mockMvc.perform(buildPostRequest(SOME_TITLE))
            .andExpect(status().isCreated())
            .andReturn().getResponse();
        
        Advertisement advertisement = convertJsonContent(response, Advertisement.class);
        advertisement.setTitle(SOME_OTHER_TITLE);
        String id = getIdFromLocation(response.getHeader(LOCATION));

        mockMvc.perform(buildPutRequest(id, advertisement))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.title", is(SOME_OTHER_TITLE)));
    }

    @Test
    public void deleteNotFound() throws Exception {
        mockMvc.perform(buildDeleteRequest("4711"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteById() throws Exception {
        String id = performPostAndGetId();
        
        mockMvc.perform(buildDeleteRequest(id))
            .andExpect(status().isNoContent());

        mockMvc.perform(buildGetRequest(id))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteAll() throws Exception {
        String id = performPostAndGetId();
        
        mockMvc.perform(buildDeleteRequest(""))
            .andExpect(status().isNoContent());

        mockMvc.perform(buildGetRequest(id))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void doNotReuseIdsOfDeletedItems() throws Exception {
        String id = performPostAndGetId();
        
        mockMvc.perform(buildDeleteRequest(id))
            .andExpect(status().isNoContent());
        
        String idNewAd = performPostAndGetId();

        assertThat(idNewAd, is(not(id)));
    }
    
    private MockHttpServletRequestBuilder buildPostRequest(String adsTitle) throws Exception {
        Advertisement advertisement = new Advertisement();
        advertisement.setTitle(adsTitle);

        // post the advertisement as a JSON entity in the request body
        return post(AdvertisementController.PATH).content(toJson(advertisement)).contentType(APPLICATION_JSON_UTF8);
    }

    private String performPostAndGetId() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(buildPostRequest(SOME_TITLE))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        return getIdFromLocation(response.getHeader(LOCATION));
    }


    private MockHttpServletRequestBuilder buildGetRequest(String id) throws Exception {
        return get(AdvertisementController.PATH + "/" + id);
    }
    
    private MockHttpServletRequestBuilder buildPutRequest(String id, Advertisement advertisement) throws Exception {
        return put(AdvertisementController.PATH + "/" + id).content(toJson(advertisement))
                .contentType(APPLICATION_JSON_UTF8);
    }

    private MockHttpServletRequestBuilder buildDeleteRequest(String id) throws Exception {
        return delete(AdvertisementController.PATH + "/" + id);
    }
    
    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private String getIdFromLocation(String location) {
        return location.substring(location.lastIndexOf('/') + 1);
    }

    private <T> T convertJsonContent(MockHttpServletResponse response, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String contentString = response.getContentAsString();
        return objectMapper.readValue(contentString, clazz);
    }
}

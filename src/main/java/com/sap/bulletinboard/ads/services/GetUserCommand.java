package com.sap.bulletinboard.ads.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.sap.bulletinboard.ads.services.UserServiceClient.User;

public class GetUserCommand extends HystrixCommand<User> {
    // Hystrix uses a default timeout of 1000 ms, increase in case you run into problems in remote locations
    private static final int DEFAULT_TIMEOUT_MS = 1000;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String url;
    private RestTemplate restTemplate;

    public GetUserCommand(String url, RestTemplate restTemplate) {
        super(HystrixCommandGroupKey.Factory.asKey("User"), DEFAULT_TIMEOUT_MS);
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    protected User run() throws Exception {
        logger.info("sending request {}", url);

        try {
            ResponseEntity<User> responseEntity = restTemplate.getForEntity(url, User.class);
            logger.info("received response, status code: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();
        } catch (HttpStatusCodeException error) {
            logger.error("received HTTP status code: {}", error.getStatusCode());
            throw error;
        }
    }

    // this will be used in exercise 18
    int getTimeoutInMs() {
        return this.properties.executionTimeoutInMilliseconds().get();
    }
}

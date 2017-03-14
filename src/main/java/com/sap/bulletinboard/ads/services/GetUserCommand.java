package com.sap.bulletinboard.ads.services;

import static com.sap.hcp.cf.logging.common.LogContext.*;

import java.util.function.Supplier;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.sap.bulletinboard.ads.services.UserServiceClient.User;
import com.sap.hcp.cf.logging.common.LogContext;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class GetUserCommand extends HystrixCommand<User> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String url;
    private RestTemplate restTemplate;
    private Supplier<User> fallbackFunction = User::new;
    private String correlationId;

    @Inject
    public GetUserCommand(RestTemplate restTemplate) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("User"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("User.getById")));
        this.restTemplate = restTemplate;
        this.correlationId = LogContext.getCorrelationId();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFallbackFunction(Supplier<User> fallbackFunction) {
        this.fallbackFunction = fallbackFunction;
    }

    @Override
    protected User run() throws Exception {
        LogContext.initializeContext(this.correlationId);
        logger.info("sending request {}", url);

        try {
            ResponseEntity<User> responseEntity = sendRequest();
            logger.info("received response, status code: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();
        } catch(HttpServerErrorException error) {
            logger.warn("received HTTP status code: {}", error.getStatusCode());
            throw error;
        } catch(HttpClientErrorException error) {
            logger.error("received HTTP status code: {}", error.getStatusCode());
            throw new HystrixBadRequestException("Unsuccessful outgoing request", error);
        }
    }

    @Override
    protected User getFallback() {
        logger.info("enter fallback method");

        LogContext.initializeContext(this.correlationId);
        if (isResponseTimedOut()) {
            logger.error("execution timed out after {} ms (HystrixCommandKey:{})", getTimeoutInMs(),
                    this.getCommandKey().name());
        }
        if (isFailedExecution()) {
            logger.error("execution failed", getFailedExecutionException());
        }
        if (isResponseRejected()) {
            logger.warn("request was rejected");
        }
        return fallbackFunction.get();
    }

    protected ResponseEntity<User> sendRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HTTP_HEADER_CORRELATION_ID, correlationId);
        HttpEntity<User> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, request, User.class);
    }

    // this will be used in exercise 18
    protected int getTimeoutInMs() {
        return this.properties.executionTimeoutInMilliseconds().get();
    }
}

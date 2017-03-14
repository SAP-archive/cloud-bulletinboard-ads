package com.sap.bulletinboard.ads.services;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.exception.HystrixRuntimeException;

@Component // defines a Spring Bean with name "userServiceClient"
public class UserServiceClient {
    private static final String PATH = "api/v1.0/users";

    // Using Spring's PropertySourcesPlaceholderConfigurer bean, get the content of the USER_ROUTE environment variable
    @Value("${USER_ROUTE}")
    private String userServiceRoute;
    private Logger logger;
    private ObjectFactory<GetUserCommand> objectFactory;

    @Inject
    public UserServiceClient(ObjectFactory<GetUserCommand> objectFactory) {
        this.logger = LoggerFactory.getLogger(getClass());
        this.objectFactory = objectFactory;
    }

    public boolean isPremiumUser(String id) throws RuntimeException {
        String url = userServiceRoute + "/" + PATH + "/" + id;
        boolean isPremiumUser = false;
        try {
            GetUserCommand getUserCommand = objectFactory.getObject(); //creates a new one, as it is declared as "Prototype"
            getUserCommand.setUrl(url);
            User user = getUserCommand.execute();
            isPremiumUser = user.premiumUser;
        } catch (HystrixRuntimeException ex) {
            logger.warn("[HystrixFailure:" + ex.getFailureType().toString() + "] " + ex.getMessage());
        }
        return isPremiumUser;
    }

    public static class User {
        // public, so that Jackson can access the field
        public boolean premiumUser;
    }
}

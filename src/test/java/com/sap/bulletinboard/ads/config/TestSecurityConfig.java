package com.sap.bulletinboard.ads.config;

import com.sap.cloud.security.adapter.spring.SAPOfflineTokenServicesCloud;
import com.sap.cloud.security.config.OAuth2ServiceConfiguration;
import com.sap.cloud.security.config.OAuth2ServiceConfigurationBuilder;
import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.config.cf.CFConstants;
import com.sap.cloud.security.test.SecurityTestRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestSecurityConfig {

    @Bean
    @Primary
    public SAPOfflineTokenServicesCloud sapOfflineTokenServices() {
        OAuth2ServiceConfiguration configuration = OAuth2ServiceConfigurationBuilder
                .forService(Service.XSUAA)
                .withClientId(SecurityTestRule.DEFAULT_CLIENT_ID)
                .withProperty(CFConstants.XSUAA.APP_ID, SecurityTestRule.DEFAULT_APP_ID)
                .withProperty(CFConstants.XSUAA.UAA_DOMAIN, SecurityTestRule.DEFAULT_DOMAIN)
                .build();
        return new SAPOfflineTokenServicesCloud(configuration).setLocalScopeAsAuthorities(true);
    }

}
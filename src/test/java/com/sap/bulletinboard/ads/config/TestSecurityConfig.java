package com.sap.bulletinboard.ads.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.sap.bulletinboard.ads.testutils.JwtGenerator;
import com.sap.xs2.security.commons.SAPOfflineTokenServices;

@Configuration
public class TestSecurityConfig {
    @Bean
    @Primary
    public SAPOfflineTokenServices sapOfflineTokenServices() {
        JwtGenerator jwtGenerator = new JwtGenerator();
        SAPOfflineTokenServices sapOfflineTokenServices = new SAPOfflineTokenServices();
        sapOfflineTokenServices.setTrustedClientId(jwtGenerator.getClientId());
        sapOfflineTokenServices.setTrustedIdentityZone(jwtGenerator.getIdentityZone());
        sapOfflineTokenServices.setVerificationKey(jwtGenerator.getPublicKey());
        sapOfflineTokenServices.afterPropertiesSet();
        return sapOfflineTokenServices;
    }
}
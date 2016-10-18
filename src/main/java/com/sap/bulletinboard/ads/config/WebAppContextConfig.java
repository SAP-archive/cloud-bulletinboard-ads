package com.sap.bulletinboard.ads.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Activates Web MVC and its @Controller classes. Triggers the search and registration of Spring bean classes annotated
 * with @Component and @Controller etc.
 */
@Configuration
@EnableWebMvc // maps @Controller annotated classes
@ComponentScan(basePackages = "com.sap.bulletinboard.ads")
public class WebAppContextConfig extends WebMvcConfigurerAdapter {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        // make environment variables available for Spring's @Value annotation
        return new PropertySourcesPlaceholderConfigurer();
    }
}
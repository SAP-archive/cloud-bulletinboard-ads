package com.sap.bulletinboard.ads;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.sap.bulletinboard.ads.config.WebAppContextConfig;
import com.sap.hcp.cf.logging.servlet.filter.RequestLoggingFilter;

public class AppInitializer implements WebApplicationInitializer {

    private static final int LOAD_ON_STARTUP = 1; // initialize when tomcat starts, not when first request comes in
    private static final String MAPPING_URL = "/*";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext applicationContext = getApplicationContext();

        // register Spring Web servlet
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet",
                new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(LOAD_ON_STARTUP);
        
        // map requests that you want the DispatcherServlet to handle, by using a URL mapping
        dispatcher.addMapping(MAPPING_URL);

        servletContext.addListener(new ContextLoaderListener(applicationContext));

        // register logging servlet filter which logs HTTP request processing details
        servletContext.addFilter("RequestLoggingFilter", RequestLoggingFilter.class).addMappingForUrlPatterns(null,
                false, "/*");
    }

    /**
     * Ensure that all required @Configuration, @Controller and @Component classes are registered to the Spring
     * application context.
     */
    private AnnotationConfigWebApplicationContext getApplicationContext() {
        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(WebAppContextConfig.class);
        applicationContext.getEnvironment().setActiveProfiles("cloud");
        return applicationContext;
    }
}

package com.sap.bulletinboard.statistics;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

import org.apache.cxf.jaxrs.spring.SpringComponentScanServer;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sap.bulletinboard.statistics.communication.IncrementCounterListener;
import com.sap.bulletinboard.statistics.config.CloudRabbitConfig;
import com.sap.bulletinboard.statistics.config.SystemEnvironment;
import com.sap.bulletinboard.statistics.resources.DefaultResource;
import com.sap.bulletinboard.statistics.resources.SendStatistics;
import com.sap.bulletinboard.statistics.resources.StatisticsResource;
import com.sap.bulletinboard.statistics.util.LoggerPostProcessor;
import com.sap.bulletinboard.statistics.util.StatisticsCounter;
import com.sap.hcp.cf.logging.servlet.filter.RequestLoggingFilter;
import com.sap.bulletinboard.statistics.util.UTF8StringConverter;

public class ContextListener implements ServletContextListener {

    private static final int LOAD_ON_STARTUP = 1; // initialized at
                                                  // initialization time
    private ContextLoaderListener wrappedListener;

    public ContextListener() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.registerShutdownHook();

        // Utilities
        context.register(JacksonJsonProvider.class);
        context.register(SpringComponentScanServer.class);
        context.register(UTF8StringConverter.class);

        // Application Classes
        context.register(LoggerPostProcessor.class);
        context.register(SystemEnvironment.class);

        // Resources
        context.register(StatisticsResource.class);
        context.register(DefaultResource.class);
        context.register(StatisticsCounter.class);

        // MQ classes
        context.register(CloudRabbitConfig.class);
        context.register(IncrementCounterListener.class);
        context.register(SendStatistics.class);

        context.refresh();
        wrappedListener = new ContextLoaderListener(context);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        registerCXF(servletContext);
        registerFilters(servletContext);

        wrappedListener.contextInitialized(event);
    }

    private void registerCXF(ServletContext servletContext) {
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("CXFServlet", CXFServlet.class);
        dispatcher.setLoadOnStartup(LOAD_ON_STARTUP);
        dispatcher.addMapping("/*");
    }

    private void registerFilters(ServletContext servletContext) {
        addFilter(servletContext, RequestLoggingFilter.class);
    }

    private void addFilter(ServletContext servletContext, Class<? extends Filter> filterClass) {
        servletContext.addFilter(filterClass.getName(), filterClass).addMappingForUrlPatterns(null, false, "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        wrappedListener.contextDestroyed(event);
    }

}

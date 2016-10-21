package com.sap.bulletinboard.ads.util;

import static org.eclipse.persistence.config.PersistenceUnitProperties.*;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.persistence.jpa.PersistenceProvider;
import org.springframework.instrument.classloading.SimpleLoadTimeWeaver;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

public class EntityManagerFactoryProvider {

    /**
     * Based on the given {@link DataSource} instance, create and configure an EntityManagerFactory. Part of this
     * configuration is that the given packages are scanned for entity classes, so that the created EntityManagers know
     * about them.
     */
    public static LocalContainerEntityManagerFactoryBean get(DataSource dataSource, String... packagesToScan) {

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceProvider(new PersistenceProvider());
        entityManagerFactoryBean.setPackagesToScan(packagesToScan);
        entityManagerFactoryBean.setDataSource(dataSource);

        // for JPA we use the classloader that Spring uses to avoid classloader issues
        entityManagerFactoryBean.setJpaPropertyMap(getJPAProperties(dataSource.getClass().getClassLoader()));
        entityManagerFactoryBean.setLoadTimeWeaver(new SimpleLoadTimeWeaver());
        entityManagerFactoryBean.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());

        entityManagerFactoryBean.afterPropertiesSet();

        return entityManagerFactoryBean;
    }

    /**
     * Set some basic properties. In our case the database schema is created (or extended) automatically. Find more
     * properties under org.eclipse.persistence.config.PersistenceUnitProperties
     */
    private static Map<String, Object> getJPAProperties(ClassLoader classLoader) {
        Map<String, Object> properties = new HashMap<>();

        properties.put(DDL_GENERATION, CREATE_OR_EXTEND);
        properties.put(DDL_GENERATION_MODE, DDL_DATABASE_GENERATION);
        properties.put(CLASSLOADER, classLoader);
        properties.put(LOGGING_LEVEL, "INFO"); // "FINE" provides more details

        // do not cache entities locally, as this causes problems if multiple application instances are used
        properties.put(CACHE_SHARED_DEFAULT, "false");

        // You can also tweak your application performance by configuring your database connection pool.
        // http://www.eclipse.org/eclipselink/documentation/2.4/jpa/extensions/p_connection_pool.htm
        properties.put(CONNECTION_POOL_MAX, 50);

        return properties;
    }
}

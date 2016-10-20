package com.sap.bulletinboard.ads.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.sap.bulletinboard.ads.models.Advertisement;
import com.sap.bulletinboard.ads.models.AdvertisementRepository;
import com.sap.bulletinboard.ads.util.EntityManagerFactoryProvider;

/**
 * See CloudDatabaseConfig for more detailed comments.
 *
 * Provides a convenient repository, based on JPA (EntityManager, TransactionManager).
 */
@Configuration
@EnableJpaRepositories(basePackageClasses = AdvertisementRepository.class)
public class EmbeddedDatabaseConfig {

    /**
     * Creates DataSource for an embedded Database (H2).
     */
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
    }

    /**
     * Based on a DataSource, provides EntityManager (JPA)
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        return EntityManagerFactoryProvider.get(dataSource, Advertisement.class.getPackage().getName());
    }

    /**
     * Based on a EntityManager, provides TransactionManager (JPA)
     */
    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
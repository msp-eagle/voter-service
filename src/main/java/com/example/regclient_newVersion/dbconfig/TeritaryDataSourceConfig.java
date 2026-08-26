package com.example.regclient_newVersion.dbconfig;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.regclient_newVersion.dataMigration.repository",
        entityManagerFactoryRef = "teritaryEntityManagerFactory",
        transactionManagerRef = "teritaryTransactionManager"
)
public class TeritaryDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.teritary")
    public DataSource teritaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean teritaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {

        Map<String, Object> properties = new HashMap<>();

        properties.put("hibernate.hbm2ddl.auto", "none");


        return builder
                .dataSource(teritaryDataSource())
                .packages("com.example.regclient_newVersion.dataMigration.entity")
                .persistenceUnit("teritary")
                .properties(properties)
                .build();
    }

    @Bean
    public PlatformTransactionManager teritaryTransactionManager(
            @Qualifier("teritaryEntityManagerFactory")
            EntityManagerFactory entityManagerFactory) {

        return new JpaTransactionManager(entityManagerFactory);
    }
}

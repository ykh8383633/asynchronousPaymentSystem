package com.example.mysql.config

import com.zaxxer.hikari.HikariDataSource
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "productEntityManagerFactory",
    transactionManagerRef = "productTransactionManager",
    basePackages = ["com.example.mysql.repository.product"]
)
class ProductDBConfig {

    @Bean("productDataSource")
    fun productDatasource(
        @Value("\${spring.datasource.product.driver-class-name}") driverClassName: String,
        @Value("\${spring.datasource.product.url}") dataSourceUrl: String,
        @Value("\${spring.datasource.product.username}") username: String,
        @Value("\${spring.datasource.product.password}") password: String
    ): DataSource {
        val datasource = DataSourceBuilder.create().type(HikariDataSource::class.java)
            .driverClassName(driverClassName)
            .url(dataSourceUrl)
            .username(username)
            .password(password)
            .build()

        datasource.poolName = "product-pool"
        datasource.connectionInitSql = "SET NAMES utf8mb4"

        return datasource
    }

    @Bean("productEntityManagerFactory")
    fun productEntityManagerFactory(
        entityManagerFactoryBuilder: EntityManagerFactoryBuilder,
        @Qualifier("productDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return entityManagerFactoryBuilder
            .dataSource(dataSource)
            .packages("com.example.mysql.entity.product")
            .persistenceUnit("product")
            .properties(mapOf(
                "hibernate.physical_naming_strategy" to CamelCaseToUnderscoresNamingStrategy::class.java.getName(),
                "hibernate.implicit_naming_strategy" to SpringImplicitNamingStrategy::class.java.getName()
            )).build()
    }

    @Bean("productTransactionManager")
    fun productTransactionManager(
        @Qualifier("productEntityManagerFactory") entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory.`object`!!)
    }

}
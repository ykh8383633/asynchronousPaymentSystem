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
    entityManagerFactoryRef = "orderEntityManagerFactory",
    transactionManagerRef = "orderTransactionManager",
    basePackages = ["com.example.mysql.repository.order", "com.example.mysql.repository.payment"]
)
class OrderDBConfig {
    @Bean("orderDataSource")
    fun orderDatasource(
        @Value("\${spring.datasource.order.driver-class-name}") driverClassName: String,
        @Value("\${spring.datasource.order.url}") dataSourceUrl: String,
        @Value("\${spring.datasource.order.username}") username: String,
        @Value("\${spring.datasource.order.password}") password: String
    ): DataSource {
        val datasource = DataSourceBuilder.create().type(HikariDataSource::class.java)
            .driverClassName(driverClassName)
            .url(dataSourceUrl)
            .username(username)
            .password(password)
            .build()

        datasource.poolName = "order-pool"
        datasource.connectionInitSql = "SET NAMES utf8mb4"

        return datasource
    }

    @Bean("orderEntityManagerFactory")
    fun orderEntityManagerFactory(
        entityManagerFactoryBuilder: EntityManagerFactoryBuilder,
        @Qualifier("orderDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return entityManagerFactoryBuilder
            .dataSource(dataSource)
            .packages("com.example.mysql.entity.order", "com.example.mysql.entity.payment")
            .persistenceUnit("order")
            .properties(mapOf(
                "hibernate.physical_naming_strategy" to CamelCaseToUnderscoresNamingStrategy::class.java.getName(),
                "hibernate.implicit_naming_strategy" to SpringImplicitNamingStrategy::class.java.getName()
            )).build()
    }

    @Bean("orderTransactionManager")
    fun orderTransactionManager(
        @Qualifier("orderEntityManagerFactory") entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory.`object`!!)
    }
}
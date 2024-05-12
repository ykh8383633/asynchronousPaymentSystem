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
    entityManagerFactoryRef = "userEntityManagerFactory",
    transactionManagerRef = "userTransactionManager",
    basePackages = ["com.example.mysql.repository.user"]
)
class UserDBConfig {

    @Bean("userDataSource")
    fun userDatasource(
        @Value("\${spring.datasource.user.driver-class-name}") driverClassName: String,
        @Value("\${spring.datasource.user.url}") dataSourceUrl: String,
        @Value("\${spring.datasource.user.username}") username: String,
        @Value("\${spring.datasource.user.password}") password: String
    ): DataSource {
        val datasource = DataSourceBuilder.create().type(HikariDataSource::class.java)
            .driverClassName(driverClassName)
            .url(dataSourceUrl)
            .username(username)
            .password(password)
            .build()

        datasource.poolName = "user-pool"
        datasource.setConnectionInitSql("SET NAMES utf8mb4")
        return datasource
    }

    @Bean("userEntityManagerFactory")
    fun userEntityManagerFactory(
        entityManagerFactoryBuilder: EntityManagerFactoryBuilder,
        @Qualifier("userDataSource") userDataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return entityManagerFactoryBuilder
            .dataSource(userDataSource)
            .packages("com.example.mysql.entity.user")
            .persistenceUnit("user")
            .properties(mapOf(
                "hibernate.physical_naming_strategy" to CamelCaseToUnderscoresNamingStrategy::class.java.getName(),
                "hibernate.implicit_naming_strategy" to SpringImplicitNamingStrategy::class.java.getName()
            )).build()
    }

    @Bean("userTransactionManager")
    fun userTransactionManager(
        @Qualifier("userEntityManagerFactory") entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): JpaTransactionManager{
        return JpaTransactionManager(entityManagerFactory.`object`!!)
    }
}
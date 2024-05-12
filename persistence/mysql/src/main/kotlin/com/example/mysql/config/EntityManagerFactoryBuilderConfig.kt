package com.example.mysql.config

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter

@Configuration
class EntityManagerFactoryBuilderConfig {

    @Bean
    fun entityManagerFactoryBuilder(): EntityManagerFactoryBuilder {
        val vendorAdaptor = HibernateJpaVendorAdapter()
        vendorAdaptor.setDatabase(Database.MYSQL)

        return EntityManagerFactoryBuilder(vendorAdaptor, mapOf<String, Any>(), null)
    }
}
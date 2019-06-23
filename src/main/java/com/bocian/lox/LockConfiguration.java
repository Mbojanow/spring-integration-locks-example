package com.bocian.lox;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;

import javax.sql.DataSource;

@Configuration
public class LockConfiguration {

    @Bean
    public DefaultLockRepository defaultLockRepository(final DataSource dataSource) {
        return new DefaultLockRepository(dataSource);
    }

    @Bean
    public JdbcLockRegistry jdbcLockRegistry(final LockRepository lockRepository) {
        return new JdbcLockRegistry(lockRepository);
    }
}

package com.example.posttracker.config;


import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUser;

    @Value("${spring.datasource.password}")
    private String databaseUserPassword;

    @Bean
    public JdbcUserDetailsManager userDetailsService() {
        PGSimpleDataSource dbSource = new PGSimpleDataSource();
        dbSource.setServerNames(null);
        dbSource.setDatabaseName(databaseUrl.substring(databaseUrl.lastIndexOf("/") + 1));
        dbSource.setUser(databaseUser);
        dbSource.setPassword(databaseUserPassword);
        return new JdbcUserDetailsManager(dbSource);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests()
                //Доступ разрешен всем пользователей
                .requestMatchers(HttpMethod.GET, "/", "/postalItem/{tracker}", "/postOffice/{index}").permitAll()
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                .requestMatchers("/swagger-resources/**",
                        "/swagger-ui.html",
                        "/v3/api-docs",
                        "/webjars/**").permitAll()

                //Все остальные страницы требуют аутентификации
                .anyRequest().authenticated().and().formLogin().loginProcessingUrl("/login");


        return http.build();
    }

}


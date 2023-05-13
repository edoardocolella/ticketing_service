package com.lab4.security.config


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests()
            .requestMatchers(("/api/**")).permitAll()
            //.requestMatchers("/api/auth/login").permitAll()
            //.requestMatchers("/api/customers/**").hasRole("CUSTOMER")
            //.requestMatchers("/api/experts/**").hasRole("EXPERT")
            //.requestMatchers("/api/managers/**").hasRole("MANAGER")
            //.requestMatchers("/api/profiles").permitAll()
            //.and().logout().permitAll()
            //.and().formLogin().disable()

        //http.csrf().disable()


        //http.oauth2ResourceServer().jwt()

        //http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        return http.build()
    }




}
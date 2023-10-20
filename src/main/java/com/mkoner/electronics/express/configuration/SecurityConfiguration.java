package com.mkoner.electronics.express.configuration;

import com.mkoner.electronics.express.filter.JwtAccessDeniedHandler;
import com.mkoner.electronics.express.filter.JwtAuthenticationEntryPoint;
import com.mkoner.electronics.express.filter.JwtAuthorizationFilter;
import com.mkoner.electronics.express.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {
    public static final String[] PUBLIC_URLS_GET = {"/api/v1/products/**", "/api/v1/categories/**", "/api/v1/files/**", "/api/v1/customers/**"};
    public static final String[] OPEN_URLS = {"/api/v1/line-items/**", "/api/v1/*/login"};
    public static final String[] PUBLIC_URLS_POST = {"/api/v1/orders", "/api/v1/admins", "/api/v1/customers"};
    public static final String[] PUBLIC_URLS_PUT = {"/api/v1/customers/open/*"};


    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;


    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;





    @Autowired
    private PasswordEncoder passwordEncoder;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization", "Token"));

        http.csrf().disable().cors().configurationSource(request -> corsConfiguration).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests().antMatchers(HttpMethod.GET, PUBLIC_URLS_GET).permitAll()
                .antMatchers(HttpMethod.POST, PUBLIC_URLS_POST).permitAll()
                .antMatchers(HttpMethod.PUT, PUBLIC_URLS_PUT).permitAll()
                .antMatchers(OPEN_URLS).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
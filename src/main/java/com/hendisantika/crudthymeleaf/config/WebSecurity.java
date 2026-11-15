package com.hendisantika.crudthymeleaf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-thymeleaf-crud
 * User: powercommerce
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 17/02/22
 * Time: 13.04
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class WebSecurity {

    private static final String ADMIN = "admin";
    private static final String USER = "user";

    private final ClientRegistrationRepository registrationRepository;
    private final KeycloakOidcUserService keycloakOidcUserService;

    public WebSecurity(ClientRegistrationRepository registrationRepository,
                       KeycloakOidcUserService keycloakOidcUserService) {
        this.registrationRepository = registrationRepository;
        this.keycloakOidcUserService = keycloakOidcUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(req -> req
                        .requestMatchers("/", "/2", "/error", "/css/**", "/images/**", "/webjars/**",
                                "/actuator/health/**").permitAll()
                        .requestMatchers("/products/add", "/products/edit/**", "/products/delete/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/products").hasRole(ADMIN)
                        .requestMatchers("/products").hasAnyRole(ADMIN, USER)
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(keycloakOidcUserService)))
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler handler =
                new OidcClientInitiatedLogoutSuccessHandler(registrationRepository);
        handler.setPostLogoutRedirectUri("{baseUrl}");
        return handler;
    }
}

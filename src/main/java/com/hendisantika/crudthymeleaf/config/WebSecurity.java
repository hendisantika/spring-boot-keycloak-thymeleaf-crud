package com.hendisantika.crudthymeleaf.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
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
//@Configuration
//@EnableWebSecurity
public class WebSecurity {
    private final InMemoryClientRegistrationRepository registrationRepository;

    public WebSecurity(InMemoryClientRegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        String[] permitAccess = new String[]{"/", "/styles/**"};

        http.authorizeRequests()
                .requestMatchers(permitAccess)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .and()
                .logout(
                        logout -> {
                            logout.logoutSuccessHandler(logoutSuccessHandler());
                            logout.invalidateHttpSession(true);
                            logout.clearAuthentication(true);
                            logout.deleteCookies("JSESSIONID");
                        });
        return http.build();
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler handler =
                new OidcClientInitiatedLogoutSuccessHandler(registrationRepository);
        handler.setPostLogoutRedirectUri("http://127.0.0.1:8888/");

        return handler;
    }
}

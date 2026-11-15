package com.hendisantika.crudthymeleaf.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-keycloak-thymeleaf-crud
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Link : s.id/hendisantika
 * Date: 15/11/26
 * Time: 17.34
 */
@SpringBootTest
@AutoConfigureMockMvc
class WebSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KeycloakOidcUserService keycloakOidcUserService;

    @Test
    void homePageIsPublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void anonymousUserIsRedirectedToKeycloakLogin() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorization/keycloak"));
    }

    @Test
    void userCanListProducts() throws Exception {
        mockMvc.perform(get("/products").with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isOk())
                .andExpect(view().name("products"));
    }

    @Test
    void userCannotCreateProduct() throws Exception {
        mockMvc.perform(get("/products/add").with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_user"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateProduct() throws Exception {
        mockMvc.perform(get("/products/add").with(oidcLogin().authorities(new SimpleGrantedAuthority("ROLE_admin"))))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"));
    }

    @Test
    void mapsKeycloakRealmAndClientRoles() {
        Map<String, Object> claims = Map.of(
                "realm_access", Map.of("roles", List.of("user")),
                "resource_access", Map.of("login-app", Map.of("roles", List.of("admin"))));

        assertThat(keycloakOidcUserService.extractRoles(claims))
                .extracting(Object::toString)
                .containsExactlyInAnyOrder("ROLE_user", "ROLE_admin");
    }
}

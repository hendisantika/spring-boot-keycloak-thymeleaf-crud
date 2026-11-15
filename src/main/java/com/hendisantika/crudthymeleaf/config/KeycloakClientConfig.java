package com.hendisantika.crudthymeleaf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.util.StringUtils;

import java.util.Map;

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
@Configuration
public class KeycloakClientConfig {

    public static final String REGISTRATION_ID = "keycloak";

    /**
     * Builds the Keycloak client registration from explicit endpoints instead of the
     * {@code issuer-uri} discovery, so the application (and its tests) can start even
     * when the Keycloak server is not reachable yet.
     */
    @Bean
    public InMemoryClientRegistrationRepository clientRegistrationRepository(
            @Value("${keycloak.auth-server-url}") String authServerUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.resource}") String clientId,
            @Value("${keycloak.credentials.secret:}") String clientSecret) {

        String issuer = authServerUrl.replaceAll("/+$", "") + "/realms/" + realm;
        String oidc = issuer + "/protocol/openid-connect";

        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(REGISTRATION_ID)
                .clientId(clientId)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email")
                .clientName("Keycloak")
                .issuerUri(issuer)
                .authorizationUri(oidc + "/auth")
                .tokenUri(oidc + "/token")
                .jwkSetUri(oidc + "/certs")
                .userInfoUri(oidc + "/userinfo")
                .userNameAttributeName("preferred_username")
                .providerConfigurationMetadata(Map.of("end_session_endpoint", oidc + "/logout"));

        if (StringUtils.hasText(clientSecret)) {
            builder.clientSecret(clientSecret)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        } else {
            // public client (PKCE is applied automatically)
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.NONE);
        }

        return new InMemoryClientRegistrationRepository(builder.build());
    }
}

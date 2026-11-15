package com.hendisantika.crudthymeleaf.config;

import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
@Component
public class KeycloakOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService delegate = new OidcUserService();
    private final String clientId;

    public KeycloakOidcUserService(@Value("${keycloak.resource}") String clientId) {
        this.clientId = clientId;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = delegate.loadUser(userRequest);

        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
        authorities.addAll(extractRoles(oidcUser.getClaims()));
        authorities.addAll(extractRoles(accessTokenClaims(userRequest.getAccessToken().getTokenValue())));

        String nameAttribute = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), nameAttribute);
    }

    /**
     * Maps Keycloak realm roles ({@code realm_access.roles}) and client roles
     * ({@code resource_access.<client>.roles}) to {@code ROLE_*} authorities.
     */
    Set<GrantedAuthority> extractRoles(Map<String, Object> claims) {
        Set<GrantedAuthority> roles = new HashSet<>();
        if (claims == null) {
            return roles;
        }
        if (claims.get("realm_access") instanceof Map<?, ?> realmAccess) {
            addRoles(roles, realmAccess.get("roles"));
        }
        if (claims.get("resource_access") instanceof Map<?, ?> resourceAccess
                && resourceAccess.get(clientId) instanceof Map<?, ?> clientAccess) {
            addRoles(roles, clientAccess.get("roles"));
        }
        return roles;
    }

    private static void addRoles(Set<GrantedAuthority> target, Object roles) {
        if (roles instanceof Collection<?> values) {
            values.forEach(role -> target.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }
    }

    /**
     * Keycloak issues JWT access tokens. The token was received directly from the token
     * endpoint over the back channel, so its claims are only read here to obtain the roles.
     */
    private static Map<String, Object> accessTokenClaims(String accessToken) {
        try {
            return JWTParser.parse(accessToken).getJWTClaimsSet().getClaims();
        } catch (ParseException e) {
            return Map.of();
        }
    }
}

package in.strikes.oauthDemo.service;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService implements
        OAuth2UserService<OidcUserRequest, OidcUser> {

    private final OidcUserService oidcUserService = new OidcUserService();

    @Autowired
    private UserService userService;

    @Override
    public @Nullable OidcUser loadUser(
            OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {

        // Let's spring do the real OIDC work
        OidcUser oidcUser =
                oidcUserService.loadUser(userRequest);

        String provider = userRequest
                .getClientRegistration()
                .getRegistrationId();

        userService.registerOrUpdate(provider, oidcUser);

        return oidcUser;
    }
}

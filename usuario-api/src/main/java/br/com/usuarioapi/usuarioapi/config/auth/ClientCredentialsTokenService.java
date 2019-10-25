package br.com.usuarioapi.usuarioapi.config.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import static java.util.Collections.singletonList;

@Service
public class ClientCredentialsTokenService {

    @Value("${app-config.services.usuario-api.url}")
    private String usuarioApiUrl;
    @Value("${app-config.oauth-clients.usuario-api.client}")
    private String oauthClient;
    @Value("${app-config.oauth-clients.usuario-api.secret}")
    private String oauthClientSecret;

    private ClientCredentialsResourceDetails resourceDetails() {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setClientId(oauthClient);
        details.setClientSecret(oauthClientSecret);
        details.setScope(singletonList("usuario-api"));
        details.setAuthenticationScheme(AuthenticationScheme.form);
        details.setAccessTokenUri(usuarioApiUrl + "/oauth/token");
        details.setClientAuthenticationScheme(AuthenticationScheme.header);
        return details;
    }

    public OAuth2AccessToken getToken() {
        return new ClientCredentialsAccessTokenProvider()
                .obtainAccessToken(resourceDetails(), new DefaultAccessTokenRequest());
    }
}

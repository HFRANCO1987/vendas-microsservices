package br.com.produtoapi.produtoapi.config.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

import static br.com.produtoapi.produtoapi.config.auth.EPermissao.ADMIN;
import static br.com.produtoapi.produtoapi.config.auth.EPermissao.USER;

@Configuration
@EnableResourceServer
public class OAuth2ResourceConfig extends ResourceServerConfigurerAdapter {

    @Value("${app-config.oauth-clients.produto-api.client}")
    private String oauthClient;
    @Value("${app-config.oauth-clients.produto-api.secret}")
    private String oauthClientSecret;
    @Value("${app-config.services.usuario-api.url}")
    private String oauthServerUrl;

    @Override
    @SuppressWarnings({"checkstyle:methodlength"})
    public void configure(HttpSecurity http) throws Exception {
        String[] permitAll = {
            "/login/**",
            "/oauth/token",
            "/oauth/authorize",
            "/api/usuarios/novo",
            "/api/clientes/endereco/**",
        };

        http
            .addFilterBefore(new CorsConfigFilter(), ChannelProcessingFilter.class)
            .requestMatchers()
            .antMatchers("/**")
            .and()
            .authorizeRequests()
            .antMatchers(permitAll).permitAll()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers("/api/categorias/**").hasAnyRole(ADMIN.name(), USER.name())
            .antMatchers("/api/fornecedores/**").hasAnyRole(ADMIN.name(), USER.name())
            .antMatchers("/api/produtos/**").hasAnyRole(ADMIN.name(), USER.name());
    }

    @Override
    public void configure(final ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices());
    }

    @Primary
    @Bean
    public RemoteTokenServices tokenServices() {
        final RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl(oauthServerUrl + "/oauth/check_token");
        tokenService.setClientId(oauthClient);
        tokenService.setClientSecret(oauthClientSecret);
        return tokenService;
    }
}

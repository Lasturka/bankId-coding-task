package cz.bankid.bankid_coding_task.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableConfigurationProperties(ApiSecurityOauthProperties.class)
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {

    private final ApiSecurityOauthProperties apiSecurityOauthProperties;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                    .pathMatchers("/health").permitAll()
                    .anyExchange().authenticated()
                )
                .oauth2Login(withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> {}));
        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String issuerUri = apiSecurityOauthProperties.issuerUri();
        NimbusReactiveJwtDecoder jwtDecoder = ReactiveJwtDecoders.fromIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(issuerUri);
        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }

}

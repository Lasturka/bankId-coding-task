package cz.bankid.bankid_coding_task.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SwaggerRedirectFix {

    @Bean
    public RouterFunction<ServerResponse> redirectToSwagger() {
        return route(GET("/"), req ->
                ServerResponse.temporaryRedirect(URI.create("/webjars/swagger-ui/index.html")).build()
        );
    }
}

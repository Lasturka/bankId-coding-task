package cz.bankid.bankid_coding_task.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

public interface HealthCheckController {

    String HEALTH_CHECK = "/health";

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "503", description = "Service is unhealthy",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @Operation(summary = "healthCheck - Check service health status",
            description = "Returns the health status of the currency service")
    @GetMapping(path = HEALTH_CHECK, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<String>> healthCheck();
}

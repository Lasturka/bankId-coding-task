package cz.bankid.bankid_coding_task.controllers;

import cz.bankid.bankid_coding_task.services.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class HealthCheckControllerImpl implements HealthCheckController {

    private final CurrencyService currencyService;

    @Override
    public Mono<ResponseEntity<String>> healthCheck() {
        return currencyService.isHealthy()
                .map(isHealthy -> isHealthy
                        ? ResponseEntity.ok("{\"status\": \"UP\"}")
                        : ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("{\"status\": \"DOWN\"}"));
    }
}

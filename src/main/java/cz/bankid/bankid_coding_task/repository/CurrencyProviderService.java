package cz.bankid.bankid_coding_task.repository;

import cz.bankid.bankid_coding_task.dto.ExchangeRatesDTO;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CurrencyProviderService {

    String getBaseURL();

    Flux<String> getAllCurrencies(WebClient webClient);

    Mono<ExchangeRatesDTO> getCurrencyExchange(WebClient webClient, String baseCurrencyCode, String quoteCurrencyCode);

    Mono<Boolean> checkConnection(WebClient webClient);
}

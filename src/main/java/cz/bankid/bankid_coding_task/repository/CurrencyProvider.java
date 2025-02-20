package cz.bankid.bankid_coding_task.repository;

import cz.bankid.bankid_coding_task.dto.providers.CNBExchangeRatesDTO;
import cz.bankid.bankid_coding_task.dto.ExchangeRatesDTO;
import cz.bankid.bankid_coding_task.dto.providers.JsdeliverExchangeRatesDTO;
import cz.bankid.bankid_coding_task.exceptions.ParseExchangeRatesException;
import cz.bankid.bankid_coding_task.mappers.CNBExchangeRateParser;
import cz.bankid.bankid_coding_task.mappers.CurrencyMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum CurrencyProvider implements CurrencyProviderService {

    CNB("https://www.cnb.cz") {

        final String xmlCurrencyRatesUri = "/cs/financni_trhy/devizovy_trh/kurzy_devizoveho_trhu/denni_kurz.xml";

        @Override
        public Flux<String> getAllCurrencies(WebClient webClient) {
            return getAndParseSourceData(webClient)
                    .flatMapMany(rates -> Flux.fromIterable(CurrencyMapper.validateAndGetRows(rates)))
                    .map(CNBExchangeRatesDTO.CurrencyRate::getCode)
                    .map(String::toLowerCase);
        }

        @Override
        public Mono<ExchangeRatesDTO> getCurrencyExchange(WebClient webClient, String baseCurrencyCode, String quoteCurrencyCode) {
            return getAndParseSourceData(webClient)
                    .flatMap(data -> CurrencyMapper.cnbDataToExchangeRatesDTO(data, baseCurrencyCode, quoteCurrencyCode));
        }

        @Override
        public Mono<Boolean> checkConnection(WebClient webClient) {
            return webClient.get()
                    .uri(xmlCurrencyRatesUri)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> response.getStatusCode().is2xxSuccessful())
                    .onErrorResume(throwable -> {
                        log.error(String.format("ČNB site %s unavailable", CNB.baseURL));
                        return Mono.just(false);
                    })
                    .defaultIfEmpty(false);
        }

        private Mono<CNBExchangeRatesDTO.ExchangeRatesData> getAndParseSourceData(WebClient webClient) {
            return webClient.get()
                    .uri(xmlCurrencyRatesUri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(CNBExchangeRateParser::parseExchangeRatesXMLData)
                    .onErrorResume(e -> {
                        String errorMessage = String.format("Unable to parse exchange rates data of ČNB provider from URL: %s.", CNB.baseURL + xmlCurrencyRatesUri);
                        log.error(errorMessage, e);
                        return Mono.error(new ParseExchangeRatesException(errorMessage));
                    });
        }

    },

    JSDELIVER("https://cdn.jsdelivr.net") {

        @Override
        public Flux<String> getAllCurrencies(WebClient webClient) {
            final String listOfCurrenciesUri = "/npm/@fawazahmed0/currency-api@latest/v1/currencies.json";
            return webClient.get()
                    .uri(listOfCurrenciesUri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                    .flatMapMany(map -> Flux.fromIterable(map.keySet()))
                    .onErrorResume(e -> {
                        String errorMessage = String.format("Unable to parse exchange rates data of JSDELIVER provider from URL: %s.", JSDELIVER.baseURL + listOfCurrenciesUri);
                        log.error(errorMessage, e);
                        return Mono.error(new ParseExchangeRatesException(errorMessage));
                    });
        }

        @Override
        public Mono<ExchangeRatesDTO> getCurrencyExchange(WebClient webClient, String baseCurrencyCode, String quoteCurrencyCode) {
            return getAndParseCurrencyExchangeData(webClient, quoteCurrencyCode)
                    .flatMap(data -> CurrencyMapper.jsdeliverDataToExchangeRatesDTO(data, baseCurrencyCode, quoteCurrencyCode));
        }

        @Override
        public Mono<Boolean> checkConnection(WebClient webClient) {
            return webClient.get()
                    .uri("/npm/@fawazahmed0/currency-api@latest/v1/currencies.json")
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> response.getStatusCode().is2xxSuccessful())
                    .onErrorResume(throwable -> {
                        log.error(String.format("JSDELIVER site %s unavailable", JSDELIVER.baseURL));
                        return Mono.just(false);
                    })
                    .defaultIfEmpty(false);
        }

        private Mono<JsdeliverExchangeRatesDTO> getAndParseCurrencyExchangeData(WebClient webClient, String quoteCurrencyCode) {
            String currencyRatesUri = String.format("/npm/@fawazahmed0/currency-api@latest/v1/currencies/%s.json", quoteCurrencyCode.toLowerCase());
            return webClient.get()
                    .uri(currencyRatesUri)
                    .retrieve()
                    .bodyToMono(JsdeliverExchangeRatesDTO.class)
                    .onErrorResume(e -> {
                        String errorMessage = String.format("Unable to parse exchange rates data of JSDELIVER provider from URL: %s for quote currency %s. Please check available currencies via endpoint getSupportedCurrencyPairsWithCNB.", JSDELIVER.baseURL + currencyRatesUri, quoteCurrencyCode.toUpperCase());
                        log.error(errorMessage);
                        return Mono.error(new ParseExchangeRatesException(errorMessage));
                    });
        }
    };

    private final String baseURL;
}

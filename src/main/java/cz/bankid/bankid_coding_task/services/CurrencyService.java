package cz.bankid.bankid_coding_task.services;

import cz.bankid.bankid_coding_task.config.WebClientConfig;
import cz.bankid.bankid_coding_task.dto.ExchangeRatesDTO;
import cz.bankid.bankid_coding_task.dto.responses.ExchangeRatesDifferenceDTO;
import cz.bankid.bankid_coding_task.dto.responses.SupportedPairsDTO;
import cz.bankid.bankid_coding_task.repository.CurrencyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final WebClientConfig webClientConfig;


    public Mono<SupportedPairsDTO> getSupportedCurrencyPairs(CurrencyProvider firstProvider, CurrencyProvider secondProvider, String baseCurrencyCode) {

        Flux<String> firstProviderAllCurrencies = getAllCurrencies(firstProvider).map(String::toUpperCase);
        Flux<String> secondProviderAllCurrencies = getAllCurrencies(secondProvider).map(String::toUpperCase);

        Flux<String> currencyIntersection = firstProviderAllCurrencies.collect(Collectors.toSet())
                .zipWith(secondProviderAllCurrencies.collect(Collectors.toSet()))
                .flatMapMany(tuple -> {
                    Set<String> set1 = tuple.getT1();
                    Set<String> set2 = tuple.getT2();
                    // intersection
                    set1.retainAll(set2);

                    return Flux.fromIterable(set1);
                });

        return currencyIntersection
                .map(quoteCurrency -> new SupportedPairsDTO.CurrencyPair(baseCurrencyCode, quoteCurrency))
                .collectList()
                .map(SupportedPairsDTO::new);

    }

    public Mono<ExchangeRatesDifferenceDTO> getExchangeRateDifference(CurrencyProvider firstProvider, CurrencyProvider secondProvider, String baseCurrencyCode, String quoteCurrencyCode) {
        Mono<ExchangeRatesDTO> firstProviderCurrencyExchange = getCurrencyExchange(firstProvider, baseCurrencyCode, quoteCurrencyCode);
        Mono<ExchangeRatesDTO> secondProviderCurrencyExchange = getCurrencyExchange(secondProvider, baseCurrencyCode, quoteCurrencyCode);

        return Mono.zip(firstProviderCurrencyExchange, secondProviderCurrencyExchange)
                .map(tuple -> {
                    ExchangeRatesDTO firstRate = tuple.getT1();
                    ExchangeRatesDTO secondRate = tuple.getT2();

                    BigDecimal secondExchangeRateAdjusted =
                            secondRate.exchangeRate()
                                    .multiply(BigDecimal.valueOf(firstRate.quotedAmount()))
                                    .divide(BigDecimal.valueOf(secondRate.quotedAmount()), RoundingMode.UNNECESSARY);

                    BigDecimal exchangeRateDifference = secondExchangeRateAdjusted.subtract(firstRate.exchangeRate());

                    return new ExchangeRatesDifferenceDTO(
                            baseCurrencyCode.toUpperCase(),
                            quoteCurrencyCode.toUpperCase(),
                            firstRate.quotedAmount(),
                            new ExchangeRatesDifferenceDTO.ExchangeProvider(firstProvider.name(), firstRate.exchangeRate()),
                            new ExchangeRatesDifferenceDTO.ExchangeProvider(secondProvider.name(), secondExchangeRateAdjusted),
                            exchangeRateDifference
                    );
                });
    }

    public Mono<Boolean> isHealthy() {
        return Mono.zip(
                checkConnection(CurrencyProvider.CNB),
                checkConnection(CurrencyProvider.JSDELIVER)
        ).map(tuple -> tuple.getT1() && tuple.getT2());
    }

    private Flux<String> getAllCurrencies(CurrencyProvider provider) {
        WebClient webClient = webClientConfig.webClient(provider);
        return provider.getAllCurrencies(webClient);
    }

    private Mono<ExchangeRatesDTO> getCurrencyExchange(CurrencyProvider provider, String baseCurrencyCode, String quoteCurrencyCode) {
        WebClient webClient = webClientConfig.webClient(provider);
        return provider.getCurrencyExchange(webClient, baseCurrencyCode, quoteCurrencyCode);
    }

    private Mono<Boolean> checkConnection(CurrencyProvider provider) {
        WebClient webClient = webClientConfig.webClient(provider);
        return provider.checkConnection(webClient);
    }

}

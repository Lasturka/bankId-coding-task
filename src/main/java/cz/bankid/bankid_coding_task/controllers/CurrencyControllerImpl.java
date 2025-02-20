package cz.bankid.bankid_coding_task.controllers;

import cz.bankid.bankid_coding_task.dto.responses.ExchangeRatesDifferenceDTO;
import cz.bankid.bankid_coding_task.dto.responses.SupportedPairsDTO;
import cz.bankid.bankid_coding_task.repository.CurrencyProvider;
import cz.bankid.bankid_coding_task.services.CurrencyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@Tag(name = "Currencies", description = "Měn")
@RequiredArgsConstructor
public class CurrencyControllerImpl implements CurrencyController {

    private final CurrencyService currencyService;

    @Override
    public Mono<ResponseEntity<SupportedPairsDTO>> getSupportedCurrencyPairsWithCNB(CurrencyProvider anotherCurrencyProvider) {
        return currencyService.getSupportedCurrencyPairs(CurrencyProvider.CNB, anotherCurrencyProvider, "CZK")
                .map(ResponseEntity.ok()::body);
    }

    @Override
    public Mono<ResponseEntity<ExchangeRatesDifferenceDTO>> getExchangeRateComparisonToCNB(CurrencyProvider currencyProvider, String quoteCurrencyCode) {
        return currencyService.getExchangeRateDifference(CurrencyProvider.CNB, currencyProvider, "CZK", quoteCurrencyCode)
                .map(ResponseEntity.ok()::body);
    }


}

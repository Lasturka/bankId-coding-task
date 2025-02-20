package cz.bankid.bankid_coding_task.controllers;

import cz.bankid.bankid_coding_task.dto.responses.ExchangeRatesDifferenceDTO;
import cz.bankid.bankid_coding_task.dto.responses.SupportedPairsDTO;
import cz.bankid.bankid_coding_task.repository.CurrencyProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface CurrencyController {

    String CURRENCY_LIST = "/currency/list";
    String CURRENCY_COMPARE= "/currency/compare";

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of currencies successfully loaded", content = @Content(schema = @Schema(implementation = SupportedPairsDTO.class))),
            @ApiResponse(responseCode = "404", description = "No supported currency found", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error communication with server", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @Operation( summary = "getCurrencyList - Get supported currency pairs which are available in ČNB and selected provider",
                description = "Vrací seznam podporovaných párů měn, které poskytuje ČNB a vybraný poskytovatel")
    @GetMapping(path = CURRENCY_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<SupportedPairsDTO>> getSupportedCurrencyPairsWithCNB(
            @Parameter(description = "Select provider to get supported currency pair intersection for both providers")
            @RequestParam CurrencyProvider currencyProvider);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exchange rate difference successfully loaded", content = @Content(schema = @Schema(implementation = ExchangeRatesDifferenceDTO.class))),
            @ApiResponse(responseCode = "404", description = "currency code not found", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Error communication with server", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @Operation( summary = "getExchangeRateDifferenceToCNB - Return exchange difference in ČNB and selected provider for CZK as base currency and selected currency.",
            description = "Pro měnový pár CZK vůči vybrané měně vrátí rozdíl v kurzu mezi ČNB a vybraným providerem")
    @GetMapping(path = CURRENCY_COMPARE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<ExchangeRatesDifferenceDTO>> getExchangeRateComparisonToCNB(
            @Parameter(description = "Currency provider, which exchange rate will be compared to one from ČNB")
            @RequestParam CurrencyProvider currencyProvider,

            @Parameter(description = "Quote currency code (case insensitive), available currencies can be listed via endpoint getSupportedCurrencyPairsWithCNB")
            @RequestParam(defaultValue = "eur") String quoteCurrencyCode);
}
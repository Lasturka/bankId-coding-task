package cz.bankid.bankid_coding_task.mappers;

import cz.bankid.bankid_coding_task.dto.providers.CNBExchangeRatesDTO;
import cz.bankid.bankid_coding_task.dto.ExchangeRatesDTO;
import cz.bankid.bankid_coding_task.dto.providers.JsdeliverExchangeRatesDTO;
import cz.bankid.bankid_coding_task.exceptions.CurrencyNotFoundException;
import cz.bankid.bankid_coding_task.exceptions.InvalidCurrencyDataException;
import cz.bankid.bankid_coding_task.utils.CurrencyUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CurrencyMapper {

    public static Mono<ExchangeRatesDTO> jsdeliverDataToExchangeRatesDTO(JsdeliverExchangeRatesDTO jsdeliverData, String baseCurrencyCode, String quoteCurrencyCode) {

        Map<String, Double> rates = jsdeliverData.getExchangeRates().get(quoteCurrencyCode.toLowerCase());

        if (rates == null)
            return Mono.error(new InvalidCurrencyDataException("Unable to load currency exchange rates data of JSDELIVER provider"));
        if (!rates.containsKey(baseCurrencyCode.toLowerCase()))
            return Mono.error(new CurrencyNotFoundException(String.format("Exchange rate not found for currency %s", baseCurrencyCode.toUpperCase())));

        BigDecimal exchangeRate = BigDecimal.valueOf(rates.get(baseCurrencyCode.toLowerCase()));
        return Mono.just(new ExchangeRatesDTO(
                baseCurrencyCode.toUpperCase(),
                quoteCurrencyCode.toUpperCase(),
                1,
                exchangeRate
        ));
    }

    public static Mono<ExchangeRatesDTO> cnbDataToExchangeRatesDTO(CNBExchangeRatesDTO.ExchangeRatesData cnbData, String baseCurrencyCode, String quoteCurrencyCode) {

        List<CNBExchangeRatesDTO.CurrencyRate> rows = validateAndGetRows(cnbData);

        return Mono.defer(() ->
                rows.stream()
                        .filter(row -> row.getCode().equalsIgnoreCase(quoteCurrencyCode))
                        .findFirst()
                        .map(rate -> Mono.just(new ExchangeRatesDTO(
                                baseCurrencyCode,
                                quoteCurrencyCode,
                                rate.getAmount(),
                                CurrencyUtils.parseDecimalFromCzechFormat(rate.getRate())
                        )))
                        .orElseGet(() -> Mono.error(new CurrencyNotFoundException(String.format("Exchange rate not found for currency %s of ČNB provider.", quoteCurrencyCode.toUpperCase()))))
        );
    }

    public static List<CNBExchangeRatesDTO.CurrencyRate> validateAndGetRows(CNBExchangeRatesDTO.ExchangeRatesData exchangeRates) {
        if (exchangeRates == null || exchangeRates.getTable() == null || exchangeRates.getTable().getRow() == null) {
            throw new InvalidCurrencyDataException("Invalid exchange rates data of ČNB provider");
        }
        return exchangeRates.getTable().getRow();
    }
}

package cz.bankid.bankid_coding_task.dto.responses;

import java.math.BigDecimal;

/**
 * DTO for representing exchange rate differences between two providers.
 */
public record ExchangeRatesDifferenceDTO(
        String baseCurrencyCode,
        String quotedCurrencyCode,
        int quotedAmount,
        ExchangeProvider firstProvider,
        ExchangeProvider secondProvider,
        BigDecimal exchangeRateDifference
) {
    /**
     * Represents an exchange rate provider with its name and rate.
     */
    public record ExchangeProvider(
            String name,
            BigDecimal exchangeRate
    ) {
    }
}
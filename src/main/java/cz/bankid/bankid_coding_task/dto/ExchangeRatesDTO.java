package cz.bankid.bankid_coding_task.dto;

import java.math.BigDecimal;

public record ExchangeRatesDTO(
        String baseCurrencyCode,
        String quotedCurrencyCode,
        int quotedAmount,
        BigDecimal exchangeRate
) {}

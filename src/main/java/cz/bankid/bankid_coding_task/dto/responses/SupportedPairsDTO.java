package cz.bankid.bankid_coding_task.dto.responses;

import java.util.List;

public record SupportedPairsDTO(List<CurrencyPair> supportedPairs) {
    public record CurrencyPair(String baseCurrencyCode, String quoteCurrencyCode) {}
}
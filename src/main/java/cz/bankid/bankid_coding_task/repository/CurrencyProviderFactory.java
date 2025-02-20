package cz.bankid.bankid_coding_task.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CurrencyProviderFactory {

    private final CurrencyProvider currencyProvider = CurrencyProvider.CNB; // default provider

    @Bean
    public CurrencyProviderService currencyProviderService() {
        return currencyProvider;
    }
}
package cz.bankid.bankid_coding_task.dto.providers;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class JsdeliverExchangeRatesDTO {

    @JsonProperty("date")
    private String date;

    private Map<String, Map<String, Double>> exchangeRates = new HashMap<>();

    @JsonAnySetter
    public void addExchangeRate(String currency, Map<String, Double> rates) {
        this.exchangeRates.put(currency, rates);
    }
}


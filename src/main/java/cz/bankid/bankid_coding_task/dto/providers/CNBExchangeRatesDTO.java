package cz.bankid.bankid_coding_task.dto.providers;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

public class CNBExchangeRatesDTO {

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "kurzy")
    @Data
    public static class ExchangeRatesData {
        @XmlElement(name = "tabulka")
        private CNBExchangeRatesDTO.CurrencyTable table;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "tabulka")
    @Data
    public static class CurrencyTable {
        @XmlElement(name = "radek")
        private List<CNBExchangeRatesDTO.CurrencyRate> row;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @Data
    public static class CurrencyRate {
        @XmlAttribute(name = "kod")
        private String code;

        @XmlAttribute(name = "mena")
        private String currency;

        @XmlAttribute(name = "mnozstvi")
        private int amount;

        @XmlAttribute(name = "kurz")
        private String rate;

        @XmlAttribute(name = "zeme")
        private String country;
    }
}

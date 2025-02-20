package cz.bankid.bankid_coding_task.mappers;

import cz.bankid.bankid_coding_task.dto.providers.CNBExchangeRatesDTO;
import cz.bankid.bankid_coding_task.exceptions.ParseExchangeRatesException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.StringReader;

@Slf4j
public class CNBExchangeRateParser {

    public static CNBExchangeRatesDTO.ExchangeRatesData parseXML(String xmlData) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(CNBExchangeRatesDTO.ExchangeRatesData.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (CNBExchangeRatesDTO.ExchangeRatesData) unmarshaller.unmarshal(new StringReader(xmlData));
    }

    public static Mono<CNBExchangeRatesDTO.ExchangeRatesData> parseExchangeRatesXMLData(String xml) {
        return Mono.fromCallable(() -> CNBExchangeRateParser.parseXML(xml))
                .onErrorMap(e -> {
                    log.error("XML parsing failed: {}", xml.substring(0, Math.min(xml.length(), 500)), e);
                    return new ParseExchangeRatesException("Error parsing XML of ČNB provider");
                });
    }

}

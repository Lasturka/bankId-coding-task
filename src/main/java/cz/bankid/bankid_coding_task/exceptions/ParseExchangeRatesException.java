package cz.bankid.bankid_coding_task.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ParseExchangeRatesException extends RuntimeException {

    /**
     * @param message   inherited from RuntimeException message
     */
    public ParseExchangeRatesException(String message) {
        super(message);
    }
}

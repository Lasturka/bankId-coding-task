package cz.bankid.bankid_coding_task.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CurrencyNotFoundException extends RuntimeException {

    /**
     * @param message   inherited from RuntimeException message
     */
    public CurrencyNotFoundException(String message) {
        super(message);
    }
}

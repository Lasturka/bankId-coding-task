package cz.bankid.bankid_coding_task.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCurrencyDataException extends RuntimeException {

    /**
     * @param message   inherited from RuntimeException message
     */
    public InvalidCurrencyDataException(String message) {
        super(message);
    }
}

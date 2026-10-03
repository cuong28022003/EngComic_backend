package mobile.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateWordException extends RuntimeException {
    public DuplicateWordException(String exception) {
        super(exception);
    }
}
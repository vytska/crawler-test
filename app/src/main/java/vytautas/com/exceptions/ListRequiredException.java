package vytautas.com.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "List of famous people is required")
public class ListRequiredException extends RuntimeException {
}

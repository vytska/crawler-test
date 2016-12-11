package vytautas.com.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Search params (url or state) are required")
public class SearchParamsRequiredException extends RuntimeException {
}

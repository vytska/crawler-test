package vytautas.com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Search params (url or unfinished) are required")
public class SearchParamsRequiredException extends RuntimeException{
}

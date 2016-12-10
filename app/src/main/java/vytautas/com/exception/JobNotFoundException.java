package vytautas.com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Job with this URL was not found")
public class JobNotFoundException extends RuntimeException{
}

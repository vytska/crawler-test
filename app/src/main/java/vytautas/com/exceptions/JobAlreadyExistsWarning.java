package vytautas.com.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.ALREADY_REPORTED, reason = "Job with this URL already exists")
public class JobAlreadyExistsWarning extends RuntimeException {
}

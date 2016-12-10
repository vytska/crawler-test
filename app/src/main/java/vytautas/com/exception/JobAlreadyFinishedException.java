package vytautas.com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Job with this URL is already finished")
public class JobAlreadyFinishedException extends RuntimeException{
}

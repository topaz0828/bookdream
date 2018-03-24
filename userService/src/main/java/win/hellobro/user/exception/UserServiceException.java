package win.hellobro.user.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class UserServiceException extends Exception {

    private static final long serialVersionUID = -3332292346834265371L;
    private HttpStatus status;
    public UserServiceException() {
        super();
    }
    public UserServiceException(HttpStatus status, String msg) {
        super(msg);
        this.status = status;
    }
}
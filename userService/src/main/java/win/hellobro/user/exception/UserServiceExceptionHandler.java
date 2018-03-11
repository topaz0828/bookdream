package win.hellobro.user.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class UserServiceExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceExceptionHandler.class);

    @ExceptionHandler(value = UserServiceException.class)
    public void handleUserServiceException(UserServiceException ex, HttpServletResponse res) {

    }

    @ResponseStatus(value= HttpStatus.NOT_FOUND, reason="IOException occured")
    @ExceptionHandler(IOException.class)
    public void handleIOException(){
        logger.error("IOException handler executed");
    }

}

package win.hellobro.user.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.sql.SQLException;

@ControllerAdvice
public class UserServiceExceptionHandler {

  //  private static final Logger logger = LoggerFactory.getLogger(UserServiceExceptionHandler.class);

    @ExceptionHandler(value = UserServiceException.class)
    @ResponseBody
    public ResponseEntity<String> handleUserServiceException(UserServiceException e) {
        return new ResponseEntity<>(e.getLocalizedMessage(), new HttpHeaders(), e.getStatus());

    }

    @ExceptionHandler({ SQLException.class, DataAccessException.class, RuntimeException.class })
    @ResponseBody
    public ResponseEntity<?> handleSQLException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ Exception.class })
    @ResponseBody
    public ResponseEntity<?> handleAnyException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ IOException.class, ParseException.class, JsonParseException.class, JsonMappingException.class })
    @ResponseBody
    public ResponseEntity<?> handleParseException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}


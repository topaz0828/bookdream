package win.hellobro.web.service.external;

public class UserServiceException extends Exception {
	UserServiceException(String message) {
		super(message);
	}

	UserServiceException(String message, Exception e) {
		super(message, e);
	}
}

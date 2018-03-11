package win.hellobro.user.exception;

public class UserServiceException extends Exception {

    private static final long serialVersionUID = -3332292346834265371L;

    public UserServiceException() {
        super();
    }
    public UserServiceException(String msg) {
        super(msg);
    }
}
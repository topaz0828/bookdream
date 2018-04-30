package win.hellobro.user.vaildator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import win.hellobro.user.exception.UserServiceException;

@Service
public class DataVaildator {

    @Value("${data-validation.len.id}")
    private int LEN_ID;

    @Value("${data-validation.len.email}")
    private int LEN_EMAIL;

    @Value("${data-validation.len.nickname}")
    private int LEN_NICKNAME;

    @Value("${data-validation.len.oauthsite}")
    private int LEN_OAUTHSITE;

	@Value("${data-validation.len.oauthId}")
	private int LEN_OAUTHID;

    @Value("${data-validation.len.image}")
    private int LEN_IMAGE;

    private int checkMaxSize(String value) {
        int maxSize = 0;
        if ("id".equalsIgnoreCase(value)) maxSize = LEN_ID;
        if ("email".equalsIgnoreCase(value)) maxSize = LEN_EMAIL;
        if ("nickname".equalsIgnoreCase(value)) maxSize = LEN_NICKNAME;
        if ("from".equalsIgnoreCase(value)) maxSize = LEN_OAUTHSITE;
        if ("image".equalsIgnoreCase(value)) maxSize = LEN_IMAGE;
	    if ("oauthId".equalsIgnoreCase(value)) maxSize = LEN_OAUTHID;
        return maxSize;
    }

    public DataVaildator isValidMandantory(String param, String value) throws UserServiceException {
        if (StringUtils.isEmpty(value))
            throw new UserServiceException(HttpStatus.BAD_REQUEST, String.format("%s [%s] is null or empty", param, value));

        if (value.length() > checkMaxSize(param))
            throw new UserServiceException(HttpStatus.BAD_REQUEST, String.format("%s [%s] data size exceed maxSize", param, value));

        return this;
    }

    public DataVaildator isValidOption(String param, String value) throws UserServiceException {
        if (value.length() > checkMaxSize(param))
            throw new UserServiceException(HttpStatus.BAD_REQUEST, String.format("%s [%s] data size exceed maxSize", param, value));

        return this;
    }


}

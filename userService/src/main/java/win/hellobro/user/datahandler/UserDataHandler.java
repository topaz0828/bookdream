package win.hellobro.user.datahandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import win.hellobro.user.datahandler.entity.UserInfo;
import win.hellobro.user.datahandler.service.IUserInfoService;
import win.hellobro.user.exception.UserServiceException;
import win.hellobro.user.vaildator.DataVaildator;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Configurable
public class UserDataHandler {

    private final static String DEFAULT_START = "0";
    private final static String DEFAULT_PAGECOUNT = "5";
    private final static String DELIMETER = ":";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataHandler.class);
   // private Map<String, UserInfo> userMap = new ConcurrentHashMap<>();

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private DataVaildator vaildator;


    public UserInfo getUserInfo(String id) throws UserServiceException {
        vaildator.isValidMandantory("id", id);

        UserInfo userInfo = userInfoService.getUserinfoById(id);
        if (userInfo == null) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, "ID is not found");
        }
        return userInfo;
    }

    public UserInfo getUserInfo(String eMail, String from) throws UserServiceException {
        vaildator.isValidMandantory("email", eMail).isValidMandantory("from", from);
/*
        String key = makeKey(eMail, from);
        if (userMap.get(key) != null) {
            return userMap.get(key);
        } else {*/
            UserInfo userInfo = userInfoService.getUserinfoByEmailAndOAuthSite(eMail, from);
            if (userInfo == null) {
                throw new UserServiceException(HttpStatus.NOT_FOUND, "Account is not found");
            }
 //           userMap.put(key, userInfo);
            return userInfo;
      //  }

    }

    public UserInfo getUserInfoByOAuthIdAndFrom(String oauthId, String from) throws UserServiceException {
        vaildator.isValidMandantory("oauthId", oauthId).isValidMandantory("from", from);

        UserInfo userInfo = userInfoService.getUserInfoByOAuthIdAndOAuthSite(oauthId, from);
        if (userInfo == null) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, "Account is not found");
        }
        return userInfo;
    }

    public void addUserInfo(String id, String nickName, String eMail, String from, String image, String oauthId) throws UnsupportedEncodingException, UserServiceException {
        vaildator.isValidMandantory("id", id)
                .isValidMandantory("nickname", nickName)
                .isValidMandantory("email", eMail)
                .isValidMandantory("from", from)
                .isValidMandantory("oauthId", oauthId)
                .isValidOption("image", image);

        UserInfo user = mappingUser(id, nickName, eMail, from, image, oauthId);
        Boolean result = userInfoService.addUserinfo(user);
        if (result) {
      //      userMap.put(makeKey(eMail, from), user);
        } else {
            throw new UserServiceException(HttpStatus.BAD_REQUEST, "already existing account");
        }
    }

    public void removeUserinfo(String eMail, String from) throws UserServiceException {
        vaildator.isValidMandantory("email", eMail)
                .isValidMandantory("from", from);

        // 수정 요망
      /*  String Key = makeKey(eMail, from);
        if (userMap.containsKey(Key)) {
            userMap.remove(Key);
        }*/
        if (!userInfoService.deleteUserInfo(eMail, from))
            throw new UserServiceException(HttpStatus.BAD_REQUEST, "cannot remove userinfo");

    }

    public List<UserInfo> getAllUserInfo(String start, String end) {
        if (StringUtils.isEmpty(start)) start = DEFAULT_START;
        if (StringUtils.isEmpty(end)) end = DEFAULT_PAGECOUNT;
        return userInfoService.getAllUserInfo(Integer.parseInt(start), Integer.parseInt(end));
    }

    private UserInfo mappingUser(String id, String nickName, String eMail, String from, String image, String oauthId)
            throws UnsupportedEncodingException {

        UserInfo user = new UserInfo();
        user.setOAuthSite(from);
        user.setOauthId(oauthId);
        user.setNickName(nickName);
        user.setEMail(eMail);
        user.setId(id);
        user.setImage(URLDecoder.decode(image, "utf8"));
        return user;
    }

    private String makeKey(String eMail, String from) {
        return String.format("%s%s%s", eMail, DELIMETER, from);
    }


    public void existEmailOrNickName(String id, String eMail, String nickName) throws UserServiceException {
        if(StringUtils.isEmpty(eMail) && StringUtils.isEmpty(nickName))
            throw new UserServiceException(HttpStatus.BAD_REQUEST, "must be input eMail or nickName");

        if (userInfoService.isExistEmail(id, eMail)) {
            throw new UserServiceException(HttpStatus.CONFLICT, "Already a Existing eMail");
        } else if (userInfoService.isExistNickName(id, nickName)) {
            throw new UserServiceException(HttpStatus.CONFLICT, "Already a Existing NickName");
        }
    }

    public UserInfo updateUserInfo(String id, UserInfo userInfo) throws UserServiceException {
        vaildator.isValidMandantory("id", id)
                .isValidOption("nickname", userInfo.getNickName())
                .isValidOption("email", userInfo.getEMail())
                .isValidOption("from", userInfo.getOAuthSite())
                .isValidOption("oauthId", userInfo.getOauthId())
                .isValidOption("image", userInfo.getImage());


        if (userInfoService.getUserinfoById(id) == null) {
            throw new UserServiceException(HttpStatus.NOT_FOUND, "User Information to modify not found");
        }
        UserInfo modifiedUser = userInfoService.updateUserInfo(id, userInfo);
        // 수정 요망.
      /*  if (!StringUtils.isEmpty(modifiedUser)) {
            String Key = makeKey(eMail, from);
            if (userMap.containsKey(Key)) {
                userMap.remove(Key);
                userMap.put(makeKey(modifiedUser.getEMail(), modifiedUser.getOAuthSite()), modifiedUser);
            }
        }*/
        return modifiedUser;
    }
}

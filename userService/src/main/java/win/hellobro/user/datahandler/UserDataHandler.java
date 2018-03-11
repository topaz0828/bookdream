package win.hellobro.user.datahandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import win.hellobro.user.exception.UserServiceException;
import win.hellobro.user.datahandler.entity.UserInfo;
import win.hellobro.user.datahandler.service.IUserInfoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Configurable
public class UserDataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataHandler.class);

    private Map<String, UserInfo> userMap = new ConcurrentHashMap<>();

    @Autowired
    private IUserInfoService userInfoService;

    public UserInfo getUserInfo(String ID) throws UserServiceException {
//        if (userMap.containsKey(ID)) {
//            return userMap.get(ID);
//        } else {
//            UserInfo userInfo = userInfoService.getUserinfoById(ID);
//            if (userInfo == null) {
//                throw new UserServiceException();
//            }
//            userMap.put(ID, userInfo);
//            return userInfo;
//        }

        UserInfo userInfo = userMap.get(ID);

        if (userInfo == null) {
            userInfo = userInfoService.getUserinfoById(ID);
            if (userInfo == null) {
                throw new UserServiceException();
            }
            userMap.put(ID, userInfo);
            return userInfo;
        } else {
            return userInfo;
        }
    }

    public void addUserInfo(String ID, String nickName, String eMail, String from) {
        UserInfo user = mappingUser(ID, nickName, eMail, from);
        userInfoService.addUserinfo(user);
        userMap.put(user.getID(), user);
    }

    public void removeUserinfo(String ID) {
        if(userMap.containsKey(ID)) {
            userMap.remove(ID);
        }
        userInfoService.deleteUserInfo(ID);
    }

    public List<UserInfo> getAllUserInfo() {
        return userInfoService.getAllUserInfo();
    }

    private  UserInfo mappingUser (String ID, String nickName, String eMail, String from) {
        UserInfo user = new UserInfo();
        user.setOAuthSite(from);
        user.setNickName(nickName);
        user.setEMail(eMail);
        user.setID(ID);
        return user;
    }
}

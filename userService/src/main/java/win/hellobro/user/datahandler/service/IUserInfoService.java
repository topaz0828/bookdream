package win.hellobro.user.datahandler.service;

import win.hellobro.user.datahandler.entity.UserInfo;

import java.util.List;

public interface IUserInfoService {
    List<UserInfo> getAllUserInfo(int start, int pageCount);

    UserInfo getUserinfoById(String id);

    boolean addUserinfo(UserInfo user);

    boolean deleteUserInfo(String eMail, String from);

    UserInfo getUserinfoByEmailAndOAuthSite(String eMail, String from);

	UserInfo getUserInfoByOAuthIdAndOAuthSite(String oauthId, String oauthSite);

    boolean isExistEmail(String id, String eMail);

    boolean isExistNickName(String id, String eMail);

    UserInfo updateUserInfo(String id, UserInfo userInfo);
}

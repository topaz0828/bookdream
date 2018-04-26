package win.hellobro.user.datahandler.service;

import win.hellobro.user.datahandler.entity.UserInfo;

import java.util.List;

public interface IUserInfoService {
    List<UserInfo> getAllUserInfo(int start, int pageCount);

    UserInfo getUserinfoById(String ID);

    boolean addUserinfo(UserInfo user);

    boolean deleteUserInfo(String eMail, String from);

    UserInfo getUserinfoByEmailAndOAuthSite(String eMail, String from);

	UserInfo getUserInfoByOAuthIdAndOAuthSite(String oauthId, String oauthSite);

    boolean isExistEmail(String eMail);

    boolean isExistNickName(String eMail);

    UserInfo updateUserInfo(String eMail, String from, UserInfo userInfo);
}

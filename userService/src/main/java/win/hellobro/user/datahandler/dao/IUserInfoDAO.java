package win.hellobro.user.datahandler.dao;

import win.hellobro.user.datahandler.entity.UserInfo;

import java.util.List;

public interface IUserInfoDAO {

    List<UserInfo> getAllUserInfoDAO(int start, int pageCount);

    UserInfo getUserInfoById(String id);

    void addUserInfo(UserInfo user);

    void deleteUserlInfo(String eMail, String id);

    boolean isExistNickName(String id, String nickName);

    boolean isExistEmail(String id, String eMail);

    boolean existEMailAndOAuthSite(String eMail, String OAuth_Site);

    UserInfo getUserInfoByEmailAndOAuthSite(String eMail, String OAuth_Site);

	UserInfo getUserInfoByOauthIdAndOAuthSite(String oauthId, String OAuth_Site);

    UserInfo updateUserinfo(String id, UserInfo userInfo);
}



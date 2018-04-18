package win.hellobro.user.datahandler.dao;

import win.hellobro.user.datahandler.entity.UserInfo;

import java.util.List;

public interface IUserInfoDAO {

    List<UserInfo> getAllUserInfoDAO(int start, int pageCount);
    UserInfo getUserInfoById(String ID);
    void addUserInfo(UserInfo user);
    void updateUserinfo(UserInfo user);
    void deleteUserlInfo(String eMail, String ID);
    boolean isExistNickName(String nickName);
    boolean isExistEmail(String eMail);
    boolean existEMailAndOAuthSite(String eMail, String OAuth_Site);
    UserInfo getUserInfoByEmailAndOAuthSite(String eMail, String OAuth_Site);
}



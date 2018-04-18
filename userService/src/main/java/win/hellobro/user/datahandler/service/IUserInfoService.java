package win.hellobro.user.datahandler.service;

import win.hellobro.user.datahandler.entity.UserInfo;

import java.util.List;

public interface IUserInfoService {
    List<UserInfo> getAllUserInfo(int start, int pageCount);
    UserInfo getUserinfoById(String ID);
    boolean addUserinfo(UserInfo user);
    void updateUserInfo(UserInfo user);
    boolean deleteUserInfo(String eMail, String from);
    UserInfo getUserinfoByEmailAndOAuthSite(String eMail, String from);
    boolean isExistEmail(String eMail);
    boolean isExistNickName(String eMail);

}

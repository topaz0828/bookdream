package win.hellobro.user.datahandler.service;

import win.hellobro.user.datahandler.entity.UserInfo;

import java.util.List;

public interface IUserInfoService {
    List<UserInfo> getAllUserInfo();
    UserInfo getUserinfoById(String ID);
    boolean addUserinfo(UserInfo user);
    void updateUserInfo(UserInfo user);
    void deleteUserInfo(String ID);
}

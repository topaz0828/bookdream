package win.hellobro.user.datahandler.dao;

import win.hellobro.user.datahandler.entity.UserInfo;

import java.util.List;

public interface IUserInfoDAO {

    List<UserInfo> getAllUserInfoDAO();
    UserInfo getUserInfoById(String ID);
    void addUserInfo(UserInfo user);
    void updateUserinfo(UserInfo user);
    void deleteUserlInfo(String ID);
    boolean existNickName(String nickName);
    boolean existUserID(String ID);

}



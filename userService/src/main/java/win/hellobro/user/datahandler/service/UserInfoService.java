package win.hellobro.user.datahandler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import win.hellobro.user.datahandler.dao.IUserInfoDAO;
import win.hellobro.user.datahandler.entity.UserInfo;

import java.util.List;


@Service
public class UserInfoService implements  IUserInfoService{

    @Autowired
    private IUserInfoDAO userInfoDAO  ;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoService.class);

    @Override
    public List<UserInfo> getAllUserInfo() {
        return userInfoDAO.getAllUserInfoDAO();
    }

    @Override
    public UserInfo getUserinfoById(String ID) {
       return  userInfoDAO.getUserInfoById(ID);
    }

    @Override
    public synchronized boolean addUserinfo(UserInfo userInfo) {
        if (userInfoDAO.existUserID(userInfo.getID())) {
            return false;
        } else {
            userInfoDAO.addUserInfo(userInfo);
            return true;
        }
    }

    @Override
    public void updateUserInfo(UserInfo userInfo) {
        userInfoDAO.updateUserinfo(userInfo);
    }

    @Override
    public void deleteUserInfo(String ID) {
        userInfoDAO.deleteUserlInfo(ID);
    }
}



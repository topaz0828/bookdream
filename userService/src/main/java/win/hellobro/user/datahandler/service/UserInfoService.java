package win.hellobro.user.datahandler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import win.hellobro.user.datahandler.dao.IUserInfoDAO;
import win.hellobro.user.datahandler.entity.UserInfo;

import java.util.List;


@Service
public class UserInfoService implements IUserInfoService {

    @Autowired
    private IUserInfoDAO userInfoDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoService.class);

    @Override
    public List<UserInfo> getAllUserInfo(int start, int pageCount) {
        return userInfoDAO.getAllUserInfoDAO(start, pageCount);
    }

    @Override
    public UserInfo getUserinfoById(String id) {
        return userInfoDAO.getUserInfoById(id);
    }


    @Override
    public UserInfo getUserinfoByEmailAndOAuthSite(String eMail, String from) {
        return userInfoDAO.getUserInfoByEmailAndOAuthSite(eMail, from);
    }

	@Override
	public UserInfo getUserInfoByOAuthIdAndOAuthSite(String oauthId, String oauthSite) {
		return userInfoDAO.getUserInfoByOauthIdAndOAuthSite(oauthId, oauthSite);
	}

	@Override
    public boolean isExistEmail(String id, String eMail) {
        return userInfoDAO.isExistEmail(id, eMail);
    }

    @Override
    public boolean isExistNickName(String id, String nickName) {
        return userInfoDAO.isExistNickName(id, nickName);
    }

    @Override
    public UserInfo updateUserInfo(String id, UserInfo userInfo) {
        return userInfoDAO.updateUserinfo(id, userInfo);
    }


    @Override
    public synchronized boolean addUserinfo(UserInfo userInfo) {
        if (userInfoDAO.existEMailAndOAuthSite(userInfo.getEMail(), userInfo.getOAuthSite())) {
            return false;
        } else {
            userInfoDAO.addUserInfo(userInfo);
            return true;
        }
    }

    @Override
    public synchronized boolean deleteUserInfo(String eMail, String from) {
        if (userInfoDAO.existEMailAndOAuthSite(eMail, from)) {
            userInfoDAO.deleteUserlInfo(eMail, from);
            return true;
        }
        return false;
    }

}



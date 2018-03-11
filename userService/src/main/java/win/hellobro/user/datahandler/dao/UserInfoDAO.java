package win.hellobro.user.datahandler.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import win.hellobro.user.datahandler.entity.UserInfo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
@Repository
public class UserInfoDAO implements IUserInfoDAO{

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoDAO.class);


    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<UserInfo> getAllUserInfoDAO() {
        String hql = "FROM UserInfo users ORDER BY users.ID";

        return (List<UserInfo>) entityManager.createQuery(hql, UserInfo.class).getResultList();
    }

    @Override
    public UserInfo getUserInfoById(String ID) {
        return entityManager.find(UserInfo.class, ID);

    }

    @Override
    public void addUserInfo(UserInfo user) {
        entityManager.persist(user);
    }

    @Override
    public void updateUserinfo(UserInfo user) {
        UserInfo updateUser = getUserInfoById(user.getID());
        updateUser.setEMail(user.getEMail());
        updateUser.setNickName(user.getNickName());
        updateUser.setOAuthSite(user.getOAuthSite());
        entityManager.flush();
    }

    @Override
    public void deleteUserlInfo(String ID) {
        entityManager.remove(getUserInfoById(ID));
    }

    @Override
    public boolean existNickName(String nickName) {
        String hql = "FROM UserInfo users WHERE users.NICKNAME = ?";
        int count = entityManager.createQuery(hql).setParameter(1, nickName).getResultList().size();
        return count > 0 ? true : false;
    }

    @Override
    public boolean existUserID(String ID) {
        String hql = "FROM UserInfo users WHERE users.ID = :userID";
        int count = entityManager.createQuery(hql).setParameter("userID", ID).getResultList().size();
        return count > 0 ? true : false;
    }
}



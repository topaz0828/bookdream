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
    public List<UserInfo> getAllUserInfoDAO(int start, int pageCount) {
        LOGGER.info("{}/{}", start, pageCount);
        String hql = "FROM UserInfo users ORDER BY 1";
         return (List<UserInfo>) entityManager.createQuery(hql, UserInfo.class)
                 .setFirstResult(start).setMaxResults(pageCount).getResultList();
    }

    @Override
    public UserInfo getUserInfoById(String ID) {
        return entityManager.find(UserInfo.class, ID);

    }

    @Override
    public void addUserInfo(UserInfo user) {
        //entityManager.remove(user);
        entityManager.merge(user);

    }

    @Override
    public void updateUserinfo(UserInfo user) {
        UserInfo updateUser = getUserInfoById(user.getId());
        updateUser.setEMail(user.getEMail());
        updateUser.setNickName(user.getNickName());
        updateUser.setOAuthSite(user.getOAuthSite());
        entityManager.flush();
    }

    @Override
    public void deleteUserlInfo(String eMail, String OAuth_Site) {
        entityManager.remove(getUserInfoByEmailAndOAuthSite(eMail,OAuth_Site));
    }

    @Override
    public boolean isExistNickName(String nickName) {
        String hql = "FROM UserInfo users WHERE users.nickName = ?";
        int count = entityManager.createQuery(hql).setParameter(1, nickName).getResultList().size();
        return count > 0 ? true : false;
    }

    @Override
    public boolean isExistEmail(String eMail) {
        String hql = "FROM UserInfo users WHERE users.nickName = ?";
        int count = entityManager.createQuery(hql).setParameter(1, eMail).getResultList().size();
        return count > 0 ? true : false;
    }


    @Override
    public boolean existEMailAndOAuthSite(String eMail, String OAuth_Site) {
        String hql = "FROM UserInfo users WHERE users.eMail = :EMAIL AND users.OAuthSite = :OAUTH_SITE";
        int count = entityManager.createQuery(hql).setParameter("EMAIL",eMail).setParameter("OAUTH_SITE", OAuth_Site).
                getResultList().size();
        return count > 0 ? true : false;
    }

    @Override
    public UserInfo getUserInfoByEmailAndOAuthSite(String eMail, String OAuth_Site) {
        String hql = "FROM UserInfo users WHERE users.eMail = :EMAIL AND users.OAuthSite = :OAUTH_SITE";
        return  (UserInfo)entityManager.createQuery(hql).setParameter("EMAIL",eMail).setParameter("OAUTH_SITE", OAuth_Site).getSingleResult();
    }
}



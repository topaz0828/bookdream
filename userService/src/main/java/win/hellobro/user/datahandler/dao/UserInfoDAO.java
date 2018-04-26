package win.hellobro.user.datahandler.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import win.hellobro.user.datahandler.entity.UserInfo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Transactional
@Repository
public class UserInfoDAO implements IUserInfoDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoDAO.class);


    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<UserInfo> getAllUserInfoDAO(int start, int pageCount) {
        LOGGER.info("{}/{}", start, pageCount);
        String hql = "FROM UserInfo users ORDER BY 1";
        return entityManager.createQuery(hql, UserInfo.class)
                .setFirstResult(start).setMaxResults(pageCount).getResultList();
    }

    @Override
    public UserInfo getUserInfoById(String ID) {
        return entityManager.find(UserInfo.class, ID);

    }

    @Override
    public void addUserInfo(UserInfo user) {
        user.setUpdateDate(getDate());
        entityManager.merge(user);

    }

    @Override
    public UserInfo updateUserinfo(String eMail, String from, UserInfo aftUser) {
        String hql = "FROM UserInfo users WHERE users.eMail = :EMAIL AND users.OAuthSite = :OAUTH_SITE";
        UserInfo user = (UserInfo) entityManager.createQuery(hql).setParameter("EMAIL", eMail).setParameter("OAUTH_SITE", from).getSingleResult();

        user.setEMail(StringUtils.isEmpty(aftUser.getEMail()) ? user.getEMail() : aftUser.getEMail());
        user.setNickName(StringUtils.isEmpty(aftUser.getNickName()) ? user.getNickName() : aftUser.getNickName());
        user.setOAuthSite(StringUtils.isEmpty(aftUser.getOAuthSite()) ? user.getOAuthSite() : aftUser.getOAuthSite());
        user.setImage(StringUtils.isEmpty(aftUser.getImage()) ? user.getImage() : aftUser.getImage());
        user.setUpdateDate(getDate());
        entityManager.flush();
        return user;
    }

    @Override
    public void deleteUserlInfo(String eMail, String OAuth_Site) {
        entityManager.remove(getUserInfoByEmailAndOAuthSite(eMail, OAuth_Site));
    }

    @Override
    public boolean isExistNickName(String nickName) {
        String hql = "FROM UserInfo users WHERE users.nickName = :NICKNAME";
        int count = entityManager.createQuery(hql).setParameter("NICKNAME", nickName).getResultList().size();
        return count > 0;
    }

    @Override
    public boolean isExistEmail(String eMail) {
        String hql = "FROM UserInfo users WHERE users.eMail = :EMAIL";
        int count = entityManager.createQuery(hql).setParameter("EMAIL", eMail).getResultList().size();
        return count > 0;
    }


    @Override
    public boolean existEMailAndOAuthSite(String eMail, String OAuth_Site) {
        String hql = "FROM UserInfo users WHERE users.eMail = :EMAIL AND users.OAuthSite = :OAUTH_SITE";
        int count = entityManager.createQuery(hql).setParameter("EMAIL", eMail).setParameter("OAUTH_SITE", OAuth_Site).
                getResultList().size();
        return count > 0;
    }

    @Override
    public UserInfo getUserInfoByEmailAndOAuthSite(String eMail, String OAuth_Site) {
        String hql = "FROM UserInfo users WHERE users.eMail = :EMAIL AND users.OAuthSite = :OAUTH_SITE";
        return (UserInfo) entityManager.createQuery(hql).setParameter("EMAIL", eMail).setParameter("OAUTH_SITE", OAuth_Site).getSingleResult();
    }

	@Override
	public UserInfo getUserInfoByOauthIdAndOAuthSite(String oauthId, String oauthSite) {
		String hql = "FROM UserInfo users WHERE users.oauthId = :OAUTH_ID AND users.OAuthSite = :OAUTH_SITE";
		List<UserInfo> result = entityManager.createQuery(hql).setParameter("OAUTH_ID", oauthId).setParameter("OAUTH_SITE", oauthSite).getResultList();
		if (!result.isEmpty()) {
			return result.get(0);
		} else {
			return null;
		}
	}

    private Date getDate() {
        Calendar calendar = Calendar.getInstance();
        return new Date(calendar.getTime().getTime());
    }

}



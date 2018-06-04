package hellobro.reviews.dao;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReviewLikeDao {
    private static final Logger log = LoggerFactory.getLogger(ReviewLikeDao.class);

    @Autowired
    private SqlSession sqlSession;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Object> select(String userId, String reiewId, String type, Integer offset, Integer limit) {
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID", userId);
        map.put("BOOK_REVIEW_ID", reiewId);
        map.put("TYPE", type);
        map.put("offset", offset);
        map.put("limit", limit);
        log.info("select parameter {}", map);
        return sqlSession.selectList("select", map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer count(String type) {
        log.info("count {}", type);
        return sqlSession.selectOne("count", type);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer countByUserId(String userId, String reviewId, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID", userId);
        map.put("BOOK_REVIEW_ID", reviewId);
        map.put("TYPE", type);
        log.info("countByUserId {}", map);
        return sqlSession.selectOne("countByUserId", map);
    }
    
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Boolean insert(String userId, String reviewId, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID", userId);
        map.put("BOOK_REVIEW_ID", reviewId);
        map.put("TYPE", type);
        try {
            sqlSession.insert("insert", map);
        } catch (Exception e) {
            log.error("insert Exception{}", e);
            return false;
        }
        log.info("insert {}", map);
        return true;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Boolean update(String userId, String reviewId, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID", userId);
        map.put("BOOK_REVIEW_ID", reviewId);
        map.put("TYPE", type);
        try {
            log.info("update {}", map);
            sqlSession.update("update", map);
        } catch (Exception e) {
            log.error("update Exception{}", e);
            return false;
        }
        return true;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Boolean delete(String userId, String reviewId) {
        try {
            if (userId != null) {
                log.info("deleteByUserId userId={}", userId);
                sqlSession.delete("deleteByUsehiddenrId", userId);
            }
            if (reviewId != null) {
                log.info("delete reviewId={}", reviewId);
                sqlSession.delete("delete", reviewId);
            }
        } catch (Exception e) {
            log.error("delete Exception{}", e);
            return false;
        }

        return true;
    }

}

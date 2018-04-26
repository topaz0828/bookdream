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
public class ReviewDao {
    private static final Logger log = LoggerFactory.getLogger(ReviewDao.class);

    @Autowired
    private SqlSession sqlSession;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Object> select(String userId, String bookId, String type, String[] isbn, Integer offset, Integer limit) {
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID", userId);
        map.put("BOOK_ID", bookId);
        map.put("TYPE", type);
        map.put("ISBN", isbn);
        map.put("offset", offset);
        map.put("limit", limit);
        log.info("select parameter {}", map);
        return sqlSession.selectList("select", map);
    }

    public Map<String, Object> selectOne(String reviewId) {
	    return sqlSession.selectOne("selectOne", reviewId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer count(String type) {
        log.info("count {}", type);
        return sqlSession.selectOne("count", type);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer countByUserId(String userId, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID", userId);
        map.put("TYPE", type);
        log.info("countByUserId {}", map);
        return sqlSession.selectOne("countByUserId", map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer countByBookId(String bookId, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("BOOK_ID", bookId);
        map.put("TYPE", type);
        log.info("countByBookId {}", map);
        return sqlSession.selectOne("countByBookId", map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Boolean insert(String userId, String bookId, String type, String text) {
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID", userId);
        map.put("BOOK_ID", bookId);
        map.put("TYPE", type);
        map.put("TEXT", text);
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
    public Boolean update(String reviewId, String userId, String bookId, String type, String text) {
        Map<String, Object> map = new HashMap<>();
        map.put("ID", reviewId);
        map.put("USER_ID", userId);
        map.put("BOOK_ID", bookId);
        map.put("TYPE", type);
        map.put("TEXT", text);
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
    public Boolean delete(String reviewId, String userId, String bookId) {
        try {
            if (reviewId != null && userId != null) {
                log.info("delete reviewId={}, userId={}", reviewId, userId);
                HashMap<String, Object> param = new HashMap<>();
                param.put("ID", reviewId);
                param.put("USER_ID", userId);
                sqlSession.delete("delete", param);
            } else if (userId != null) {
                log.info("deleteByUserId userId={}", userId);
                sqlSession.delete("deleteByUserId", userId);
            } else if (bookId != null) {
                log.info("deleteByBookId bookId={}", bookId);
                sqlSession.delete("deleteByBookId", bookId);
            }
        } catch (Exception e) {
            log.error("delete Exception{}", e);
            return false;
        }

        return true;
    }

}

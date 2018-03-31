package hellobro.reviews.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReviewDao {

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

	    System.out.println("select parameter : " + map);
        return sqlSession.selectList("select", map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer count(String type) {
        return sqlSession.selectOne("count", type);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer countByUserId(String userId, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID", userId);
        map.put("TYPE", type);
        return sqlSession.selectOne("countByUserId", map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer countByBookId(String bookId, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("BOOK_ID", bookId);
        map.put("TYPE", type);
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
        }
        catch (Exception e){
        	e.printStackTrace();
            return false;
        }
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
            sqlSession.update("update", map);
        }
        catch (Exception e){
	        e.printStackTrace();
            return false;
        }
        return true;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Boolean delete(String reviewId, String userId, String bookId) {
        try {
            if(reviewId != null){
                sqlSession.delete("delete", reviewId);
            }
            if(userId != null){
                sqlSession.delete("deleteByUserId", userId);
            }
            if(bookId != null){
                sqlSession.delete("deleteByBookId", bookId);
            }
        }
        catch (Exception e){
	        e.printStackTrace();
            return false;
        }
        return true;
    }

}

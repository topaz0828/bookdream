package hellobro.reviews.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReviewDao {

    @Autowired
    private SqlSession sqlSession;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Object> select(String userid, String bookid, String type, String[] isbn, Integer offset, Integer limit) {
        Map<String, Object> map = new HashMap<>();
        map.put("USER_ID", userid);
        map.put("BOOK_ID", bookid);
        map.put("TYPE", type);
        map.put("ISBN", isbn);
        map.put("offset", offset);
        map.put("limit", limit);
        return sqlSession.selectList("select", map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer count() {
        System.out.println("#### sqlSession:" + sqlSession);
        System.out.println("#### " + this + "::count");
        return sqlSession.selectOne("count");
    }
}

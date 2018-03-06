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
    public List<Object> select() {
        return sqlSession.selectList("select");
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer count() {
        System.out.println("#### sqlSession:" + sqlSession);
        System.out.println("#### " + this + "::count");
        return sqlSession.selectOne("count");
    }
}

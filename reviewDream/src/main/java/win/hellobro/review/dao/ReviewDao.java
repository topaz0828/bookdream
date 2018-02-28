package win.hellobro.review.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewDao {

    @Autowired
    private SqlSession sqlSession;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Object> select() {
        System.out.println("sqlSession:" + sqlSession);
        System.out.println(this + "::select");
//        return sqlSession.selectList("select");
        return new ArrayList<>();
    }

}

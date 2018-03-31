package hellobro.reviews.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class BookDao {

    @Autowired
    private SqlSession sqlSession;

    public Boolean insert(int pubId, int categoryId, String title, String description, String image, String author,
                          String translator, String price, String rarity, int pages, String isbn) {
        Map<String, Object> map = new HashMap<>();
        map.put("PUB_ID", pubId);
        map.put("CATEGORY_ID", categoryId);
        map.put("TITLE", title);
        map.put("DESCRIPTION", description);
        map.put("IMAGE", image);
        map.put("AUTHOR", author);
        map.put("TRANSLATOR", translator);
        map.put("PRICE", price);
        map.put("RARITY", rarity);
        map.put("PAGES", pages);
        map.put("ISBN", isbn);
        return insert2(map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Boolean insert2(Map<String, Object> map) {
        try {
            sqlSession.insert("insertBook", map);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public Integer selectCategoryId(String name) {
	    try {
		    return sqlSession.selectOne("selectBookCategory", name);
	    } catch (Exception e) {
		    System.out.println(e);
		    return null;
	    }
    }

    public void insertCategory(String name) {
	    try {
		    sqlSession.insert("insertBookCategory", name);
	    } catch (Exception e) {
		    System.out.println(e);
	    }
    }

    public Integer selectBookId(String isbn) {
	    try {
		    return sqlSession.selectOne("selectBookId", isbn);
	    } catch (Exception e) {
		    System.out.println(e);
		    return null;
	    }
    }
}

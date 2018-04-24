package hellobro.reviews.dao;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class BookDao {
    private static final Logger log = LoggerFactory.getLogger(BookDao.class);

    @Autowired
    private SqlSession sqlSession;

    public Integer insert1(int pubId, int categoryId, String title, String description, String image, String author,
                           String translator, String price, String rarity, int pages, String isbn) {
        Map<String, Object> map = new HashMap<>();
        map.put("pub_id", pubId);
        map.put("CATEGORY_ID", categoryId);
        map.put("TITLE", title);
        map.put("DESCRIPTION", description);
        map.put("image", image);
        map.put("AUTHOR", author);
        map.put("TRANSLATOR", translator);
        map.put("PRICE", price);
        map.put("RARITY", rarity);
        map.put("PAGES", pages);
        map.put("ISBN", isbn);
        return insert2(map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer insert2(Map<String, Object> map) {
        try {
            sqlSession.insert("insertBook", map);
            log.info("insertBook {}", map);
            return sqlSession.selectOne("selectBookId2", map);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return null;
    }

    public Integer selectCategoryId(String name) {
        try {
            log.info("selectCategoryId {}", name);
            return sqlSession.selectOne("selectBookCategory", name);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return null;
    }

    public void insertCategory(String name) {
        try {
            sqlSession.insert("insertBookCategory", name);
            log.info("insertCategory {}", name);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }

    public Integer selectBookId(String isbn) {
        try {
            log.info("selectBookId {}", isbn);
            return sqlSession.selectOne("selectBookId", isbn);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return null;
    }

	public void insertPublisher(String name) {
		try {
			sqlSession.insert("insertPublisher", name);
			log.info("insertPublisher {}", name);
		} catch (Exception e) {
			log.error("insertPublisher Exception", e);
		}
	}

	public Map<String, Object> selectPublisher(String name) {
		try {
			log.info("selectPublisher {}", name);
			return sqlSession.selectOne("selectPublisher", name);
		} catch (Exception e) {
			log.error("selectPublisher Exception", e);
		}
		return null;
	}
}

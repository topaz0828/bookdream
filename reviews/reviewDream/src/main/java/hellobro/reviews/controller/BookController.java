package hellobro.reviews.controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import hellobro.reviews.dao.BookDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Map;

@RestController
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    static private JsonParser jsonParser = new JsonParser();
    static private Gson gson = new GsonBuilder().create();
//    final private int DEFAULT_OFFSET = 0;
//    final private int DEFAULT_LIMIT = 25;

    @Autowired
    private BookDao dao;

    //curl -d '{"PUB_ID":2,"CATEGORY_ID":3,"TITLE":"TITLE","DESCRIPTION":"DESCRIPTION","IMAGE":"IMAGE","AUTHOR":"AUTHOR","TRANSLATOR":"TRANSLATOR","PRICE":"PRICE","RARITY":"5","PAGES":0,"ISBN":NULL}' -H "Content-Type: application/json" localhost:8080/book2
    @RequestMapping(value = {"/book"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Integer> insertBook(@RequestBody String body) {
        JsonObject json = (JsonObject) jsonParser.parse(body);
        Type stringObjectMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> map = gson.fromJson(json, stringObjectMap);
        return insertBook(map);
    }

    @RequestMapping(value = {"/book-id"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Integer> getBookId(@RequestParam(value = "isbn", required = false) String isbn) {
        Integer bookId = this.dao.selectBookId(isbn);
        if (bookId == null) {
            return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(bookId, HttpStatus.OK);
        }
    }

    private ResponseEntity<Integer> insertBook(Map<String, Object> map) {
	    Long publisherId = findPublisherId((String) map.remove("PUB"));
	    map.put("pub_id", publisherId);

        String category = (String) map.remove("CATEGORY");
        if (category == null) {
            Integer bookId = dao.insert2(map);
            if (bookId == null) {
                return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(bookId, HttpStatus.OK);
        } else {
            Integer categoryId = findCategoryId(category);
	        if (categoryId == null) {
		        return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
	        } else {
		        map.put("CATEGORY_ID", categoryId);
	        }

            Integer bookId = dao.insert2(map);
            if (bookId == null) {
                return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(bookId, HttpStatus.OK);
        }
    }

    private Integer findCategoryId(String category) {
	    Integer categoryId = this.dao.selectCategoryId(category);
	    if (categoryId == null) {
		    this.dao.insertCategory(category);
		    return this.dao.selectCategoryId(category);
	    }
	    return null;
    }

    private Long findPublisherId(String publisher) {
	    Map<String, Object> result = dao.selectPublisher(publisher);
		if (result == null) {
			dao.insertPublisher(publisher);
			result = dao.selectPublisher(publisher);
			return (Long) result.get("ID");
		}
		return null;
    }
}

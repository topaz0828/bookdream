package hellobro.reviews.controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hellobro.reviews.dao.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
public class ReviewsController {

    static private JsonParser jsonParser = new JsonParser();
    static private Gson gson = new GsonBuilder().create();
    final private int DEFAULT_OFFSET = 0;
    final private int DEFAULT_LIMIT = 25;

    @Autowired
    private ReviewDao dao;

    @RequestMapping(value = {"user/{uid}/review", "book/{bid}/review"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> getReviewReq(@PathVariable("uid") String userId,
                                               @PathVariable("bid") String bookid,
                                               @RequestParam(value = "type", required = false) String type,
                                               @RequestParam(value = "isbn", required = false) String[] isbn,
                                               @RequestParam(value = "offset", required = false) Integer offset,
                                               @RequestParam(value = "limit", required = false) Integer limit) {
        return getReview(userId, bookid, type, isbn, offset, limit);
    }

    @RequestMapping(value = {"/review"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> getReviewReq2(@RequestParam(value = "userId", required = false) String userId,
                                                @RequestParam(value = "bookid", required = false) String bookid,
                                                @RequestParam(value = "type", required = false) String type,
                                                @RequestParam(value = "isbn", required = false) String[] isbn,
                                                @RequestParam(value = "offset", required = false) Integer offset,
                                                @RequestParam(value = "limit", required = false) Integer limit) {
        return getReview(userId, bookid, type, isbn, offset, limit);
    }
	@RequestMapping(value = {"/review/{reviewId}"}, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> getReivew(@PathVariable("reviewId") String reviewId) {
    	Map<String, Object> review = this.dao.selectOne(reviewId);
		return new ResponseEntity<>(gson.toJson(review), HttpStatus.OK);
	}


    @RequestMapping(value = {"user/{uid}/review/count"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Integer> getReviewReqCount1(@PathVariable(value = "uid", required = false) String userId,
                                                      @RequestParam(value = "type", required = false) String type) {
        return getReviewCountByUserId(userId, type);
    }

    @RequestMapping(value = {"book/{bid}/review/count"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Integer> getReviewReqCount2(@PathVariable(value = "bid", required = false) String bookId,
                                                      @RequestParam(value = "type", required = false) String type) {
        return getReviewCountByBookId(bookId, type);
    }

    @RequestMapping(value = {"/review/count"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Integer> getReviewReqCount3(@RequestParam(value = "type", required = false) String type) {
        return getReviewCount(type);
    }

    //insertReview()
    //- curl -X POST URL/review -d '{"USER_ID":"usertest", "USER_ID":0, "TYPE":"C", "TEXT":"it's fun!!"}'
    @RequestMapping(value = {"user/{uid}/review", "book/{bid}/review", "/review"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> insertReview(@PathVariable(value = "uid", required = false) String userId,
                                                @PathVariable(value = "bid", required = false) String bookId,
                                                @RequestBody String body) {
        JsonObject json = (JsonObject) jsonParser.parse(body);
        userId = verifyString("USER_ID", userId, json);
        bookId = verifyString("BOOK_ID", bookId, json);
        return insertReview(userId, bookId, json.get("TYPE").getAsString(), json.get("TEXT").getAsString());
    }

    @RequestMapping(value = {"user/{uid}/review", "book/{bid}/review", "/review/{rId}"}, method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> updateReview(@PathVariable(value = "rId", required = false) String reviewId,
                                                @PathVariable(value = "uid", required = false) String userId,
                                                @PathVariable(value = "bid", required = false) String bookId,
                                                @RequestBody String body) {
        JsonObject json = (JsonObject) jsonParser.parse(body);
        userId = verifyString("USER_ID", userId, json);
        bookId = verifyString("BOOK_ID", bookId, json);
        reviewId = verifyString("ID", reviewId, json);
        return updateReview(reviewId, userId, bookId, json.get("TYPE").getAsString(), json.get("TEXT").getAsString());
    }

    @RequestMapping(value = {"user/{uid}/review", "book/{bid}/review", "/review/{rId}"}, method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> deleteReview(@PathVariable(value = "rId", required = false) String reviewId,
                                                @PathVariable(value = "uid", required = false) String userId,
                                                @PathVariable(value = "bid", required = false) String bookId,
                                                @RequestBody String body) {
        JsonObject json = (JsonObject) jsonParser.parse(body);
        userId = verifyString("USER_ID", userId, json);
        bookId = verifyString("BOOK_ID", bookId, json);
        reviewId = verifyString("ID", reviewId, json);
        return deleteReview(reviewId, userId, bookId);
    }

    private String verifyString(String key, String checkValue, JsonObject json) {
        if (json == null || json.get(key) == null) {
            return checkValue;
        }
        if (checkValue == null) {
            return json.get(key).getAsString();
        }
        return checkValue;
    }

    private ResponseEntity<String> getReview(String userId, String bookId, String type, String[] isbn, Integer offset, Integer limit) {
        if (offset == null)
            offset = DEFAULT_OFFSET;
        if (limit == null)
            limit = DEFAULT_LIMIT;
        ArrayList<Object> reviews = (ArrayList) dao.select(userId, bookId, type, isbn, offset, limit);
        JsonArray jsonArray = gson.toJsonTree(reviews).getAsJsonArray();
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }


    private ResponseEntity<Integer> getReviewCount(String type) {
        return new ResponseEntity<>(dao.count(type), HttpStatus.OK);
    }

    private ResponseEntity<Integer> getReviewCountByUserId(String userId, String type) {
        return new ResponseEntity<>(dao.countByUserId(userId, type), HttpStatus.OK);
    }

    private ResponseEntity<Integer> getReviewCountByBookId(String bookId, String type) {
        return new ResponseEntity<>(dao.countByBookId(bookId, type), HttpStatus.OK);
    }


    private ResponseEntity<Boolean> insertReview(String userId, String bookId, String type, String text) {
        return new ResponseEntity<>(dao.insert(userId, bookId, type, text), HttpStatus.OK);
    }

    private ResponseEntity<Boolean> updateReview(String reviewId, String userId, String bookId, String type, String text) {
        return new ResponseEntity<>(dao.update(reviewId, userId, bookId, type, text), HttpStatus.OK);
    }

    private ResponseEntity<Boolean> deleteReview(String reviewId, String userId, String bookId) {
        return new ResponseEntity<>(dao.delete(reviewId, userId, bookId), HttpStatus.OK);
    }

}

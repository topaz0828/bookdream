package hellobro.reviews.controller;


import com.google.gson.*;
import hellobro.reviews.dao.ReviewLikeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class ReviewLikeController {

    static private JsonParser jsonParser = new JsonParser();
    static private Gson gson = new GsonBuilder().create();
    final private int DEFAULT_OFFSET = 0;
    final private int DEFAULT_LIMIT = 25;

    @Autowired
    private ReviewLikeDao dao;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = {"user/{userId}/like", "book/{bookId}/like", "review/{reviewId}/like"}, method = RequestMethod.GET)
    public ResponseEntity<String> getReviewReq(@PathVariable("userId") String userId,
                                               @PathVariable("reviewId") String reviewId,
                                               @RequestParam(value = "type", required = false) String type,
                                               @RequestParam(value = "offset", required = false) Integer offset,
                                               @RequestParam(value = "limit", required = false) Integer limit) {
        return getReviewLike(userId, reviewId, type, offset, limit);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = {"/like"}, method = RequestMethod.GET)
    public ResponseEntity<String> getReviewReq2(@RequestParam(value = "userId", required = false) String userId,
                                                @RequestParam(value = "reviewId", required = false) String reviewId,
                                                @RequestParam(value = "type", required = false) String type,
                                                @RequestParam(value = "offset", required = false) Integer offset,
                                                @RequestParam(value = "limit", required = false) Integer limit) {
        return getReviewLike(userId, reviewId, type, offset, limit);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = {"user/{userId}/like/count", "book/{bookId}/like/count", "review/{reviewId}/like/count"}, method = RequestMethod.GET)
    public ResponseEntity<Integer> getReviewReqCount1(@PathVariable(value = "userId", required = false) String userId,
                                                      @PathVariable(value = "reviewId", required = false) String reviewId,
                                                      @RequestParam(value = "type", required = false) String type) {
        return getReviewLikeCount(userId, reviewId, type);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = {"like/count"}, method = RequestMethod.GET)
    public ResponseEntity<Integer> getReviewReqCount2(@RequestParam(value = "type", required = false) String type) {
        return getReviewLikeCount(null, null, type);
    }


    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = {"user/{userId}/like", "review/{reviewId}/like", "/like"}, method = RequestMethod.POST)
    public ResponseEntity<Boolean> insertReview(@PathVariable(value = "userId", required = false) String userId,
                                                @PathVariable(value = "reviewId", required = false) String reviewId,
                                                @RequestBody String body) {
        JsonObject json = (JsonObject) jsonParser.parse(body);
        userId = choiceValue(userId, json, "USER_ID");
        reviewId = choiceValue(reviewId, json, "BOOK_REVIEW_ID");
        return insertReviewLike(userId, reviewId, json.get("TYPE").getAsString());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = {"user/{userId}/like", "review/{reviewId}/like"}, method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updateReview(@PathVariable(value = "reviewId", required = false) String reviewId,
                                                @PathVariable(value = "userId", required = false) String userId,
                                                @RequestBody String body) {
        JsonObject json = (JsonObject) jsonParser.parse(body);
        userId = choiceValue(userId, json, "USER_ID");
        reviewId = choiceValue(reviewId, json, "BOOK_REVIEW_ID");
        return updateReviewLike(userId, reviewId, json.get("TYPE").getAsString());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = {"user/{userId}/like", "/review/{reviewId}"}, method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteReview(@PathVariable(value = "userId", required = false) String userId,
                                                @PathVariable(value = "reviewId", required = false) String reviewId,
                                                @RequestBody String body) {
        JsonObject json = (JsonObject) jsonParser.parse(body);
        userId = choiceValue(userId, json, "USER_ID");
        reviewId = choiceValue(reviewId, json, "BOOK_REVIEW_ID");
        return deleteReviewLike(userId, reviewId);
    }

    private ResponseEntity<String> getReviewLike(String userId, String reviewId, String type, Integer offset, Integer limit) {
        if (offset == null)
            offset = DEFAULT_OFFSET;
        if (limit == null)
            limit = DEFAULT_LIMIT;
        ArrayList<Object> reviews = (ArrayList) dao.select(userId, reviewId, type, offset, limit);
        JsonArray jsonArray = gson.toJsonTree(reviews).getAsJsonArray();
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }


    private ResponseEntity<Integer> getReviewLikeCount(String userId, String reviewId, String type) {
        return new ResponseEntity<>(dao.countByUserId(userId, reviewId, type), HttpStatus.OK);
    }

    private ResponseEntity<Boolean> insertReviewLike(String userId, String reviewId, String type) {
        return new ResponseEntity<>(dao.insert(userId, reviewId, type), HttpStatus.OK);
    }

    private ResponseEntity<Boolean> updateReviewLike(String userId, String reviewId, String type) {
        return new ResponseEntity<>(dao.update(userId, reviewId, type), HttpStatus.OK);
    }

    private ResponseEntity<Boolean> deleteReviewLike(String userId, String reviewId) {
        return new ResponseEntity<>(dao.delete(userId, reviewId), HttpStatus.OK);
    }


    private String choiceValue(String pathVariable, JsonObject json, String key) {
        if (json == null || json.get(key) == null) {
            return pathVariable;
        }
        if (pathVariable == null) {
            return json.get(key).getAsString();
        }
        return pathVariable;
    }

}

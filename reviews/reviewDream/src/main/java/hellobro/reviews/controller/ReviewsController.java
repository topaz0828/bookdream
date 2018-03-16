package hellobro.reviews.controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import hellobro.reviews.dao.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class ReviewsController {

    final private int DEFAULT_OFFSET = 0;
    final private int DEFAULT_LIMIT = 25;
    Gson gson = new GsonBuilder().create();
    @Autowired
    private ReviewDao dao;

    @RequestMapping(value = {"user/{uid}/review", "book/{bid}/review"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> getReviewReq(@PathVariable("uid") String userid,
                                               @PathVariable("bid") String bookid,
                                               @RequestParam(value = "type", required = false) String type,
                                               @RequestParam(value = "isbn", required = false) String[] isbn,
                                               @RequestParam(value = "offset", required = false) Integer offset,
                                               @RequestParam(value = "limit", required = false) Integer limit) {
        return getReview(userid, bookid, type, isbn, offset, limit);
    }

    @RequestMapping(value = {"/review"}, method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<String> getReviewReq2(@RequestParam(value = "userid", required = false) String userid,
                                                @RequestParam(value = "bookid", required = false) String bookid,
                                                @RequestParam(value = "type", required = false) String type,
                                                @RequestParam(value = "isbn", required = false) String[] isbn,
                                                @RequestParam(value = "offset", required = false) Integer offset,
                                                @RequestParam(value = "limit", required = false) Integer limit) {
        return getReview(userid, bookid, type, isbn, offset, limit);
    }

    private ResponseEntity<String> getReview(String userid, String bookid, String type, String[] isbn, Integer offset, Integer limit) {
        if (offset == null)
            offset = DEFAULT_OFFSET;
        if (limit == null)
            limit = DEFAULT_LIMIT;
        ArrayList<Object> reviews = (ArrayList) dao.select(userid, bookid, type, isbn, offset, limit);
        JsonArray jsonArray = gson.toJsonTree(reviews).getAsJsonArray();
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

}

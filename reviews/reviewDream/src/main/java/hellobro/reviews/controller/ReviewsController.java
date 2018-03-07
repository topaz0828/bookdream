package hellobro.reviews.controller;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hellobro.reviews.dao.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class ReviewsController {

    @Autowired
    private ReviewDao dao;

    @RequestMapping(value = "/reviews", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public String getReviews() {
        ArrayList<HashMap<String, Object>> reviews = (ArrayList) dao.select();

        JsonArray jsonArray = new JsonArray();
        for (int i = 0 ; i < reviews.size() ; i++){
            HashMap<String, Object> map = reviews.get(i);
            JsonObject json = makeMap2Json(map);
            jsonArray.add(json);
        }
        return jsonArray.toString();
    }

    private JsonObject makeMap2Json(HashMap<String, Object> map) {
        JsonObject json = new JsonObject();
        for( HashMap.Entry<String, Object> entry : map.entrySet() ) {
            json.addProperty(entry.getKey(), entry.getValue().toString());
        }
        return json;
    }

}

package hellobro.reviews.controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import hellobro.reviews.dao.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.Map;

@RestController
public class BookController {

    static private JsonParser jsonParser = new JsonParser();
    static private Gson gson = new GsonBuilder().create();
    final private int DEFAULT_OFFSET = 0;
    final private int DEFAULT_LIMIT = 25;

    @Autowired
    private BookDao dao;

    //curl -d '{"PUB_ID":2,"CATEGORY_ID":3,"TITLE":"TITLE","DESCRIPTION":"DESCRIPTION","IMAGE":"IMAGE","AUTHOR":"AUTHOR","TRANSLATOR":"TRANSLATOR","PRICE":"PRICE","RARITY":"5","PAGES":0,"ISBN":NULL}' -H "Content-Type: application/json" localhost:8080/book2
    @RequestMapping(value = {"/book"}, method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Boolean> insertBook(@RequestBody String body) {
        JsonObject json = (JsonObject) jsonParser.parse(body);
        Type stringStringMap = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> map = gson.fromJson(json, stringStringMap);
        return insertBook(map);
    }

    private ResponseEntity<Boolean> insertBook(Map<String, Object> map) {
        return new ResponseEntity<>(dao.insert2(map), HttpStatus.OK);
    }

    private String getBookInfoValue(JsonObject json, String key, String def) {
        return json.get(key) == null ? def : json.get(key).getAsString();
    }

    private int getBookInfoValue(JsonObject json, String key, int def) {
        return json.get(key) == null ? def : json.get(key).getAsInt();
    }
}

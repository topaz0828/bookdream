package win.hellobro.review.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import win.hellobro.review.dao.ReviewDao;

@RestController
public class ReviewsController {

    @Autowired
    private ReviewDao dao;

    @RequestMapping(value = "/reviews", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public String getReviews() {
        System.out.println("dao:" + dao);
        System.out.println(dao.select());
        return "get reviews list";
    }

}

package win.hellobro.user.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import win.hellobro.user.exception.UserServiceException;
import win.hellobro.user.datahandler.UserDataHandler;
import win.hellobro.user.datahandler.entity.UserInfo;

import java.util.List;


@RestController
@RequestMapping("/")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserDataHandler userDataHandler;

    @RequestMapping(value = "user/{ID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public UserInfo getUserInfo(@PathVariable String ID) throws UserServiceException {
        return userDataHandler.getUserInfo(ID);
    }

    //페이징 처리   (limit) /
    @RequestMapping(value = "users", method = RequestMethod.GET)
    public List<UserInfo> getAllUsers() {
        return userDataHandler.getAllUserInfo();
    }

    //가입 : POST
    //email=ksm@test.com&nickname=nero&from=facebook (querystring)
    @RequestMapping(value = "user", method = RequestMethod.POST)
    public ResponseEntity<Void> addUser(@RequestParam(name = "ID", required = true) String ID,
                                        @RequestParam(name = "nickname", required = true) String nickName,
                                        @RequestParam(name = "email", required = true) String eMail,
                                        @RequestParam(name = "from", required = false) String from) {

        LOGGER.info("input : [{}/{}/{}/{}]", ID, nickName, eMail, from);
        userDataHandler.addUserInfo(ID, nickName, eMail, from);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

   /* @RequestMapping(value = "/{ID}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserInfo> updateArticle(@RequestBody UserInfo userInfo) {
        articleService.updateArticle(article);
        return new ResponseEntity<Article>(article, HttpStatus.OK);
    }*/

    @RequestMapping(value = "user/{ID}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUserInfo(@PathVariable String ID) throws UserServiceException {
        userDataHandler.removeUserinfo(ID);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}

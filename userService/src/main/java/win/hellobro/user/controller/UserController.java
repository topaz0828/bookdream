package win.hellobro.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import win.hellobro.user.datahandler.UserDataHandler;
import win.hellobro.user.datahandler.entity.UserInfo;
import win.hellobro.user.exception.UserServiceException;
import win.hellobro.user.vaildator.DataVaildator;

import java.io.UnsupportedEncodingException;
import java.util.List;


@RestController
@RequestMapping("/")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserDataHandler userDataHandler;

    @Autowired
    DataVaildator dataVaildator;

    @RequestMapping(value = "user/{ID}", method = RequestMethod.GET)
    public UserInfo getUserInfo(@PathVariable String ID) throws UserServiceException {
        return userDataHandler.getUserInfo(ID);
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public UserInfo getUserInfoByEmailnFrom(@RequestParam(name = "email", required = true) String eMail,
                                            @RequestParam(name = "from", required = true) String from) throws UserServiceException {

       return userDataHandler.getUserInfo(eMail, from);
    }


    @RequestMapping(value = "users", method = RequestMethod.GET)
    public List<UserInfo> getAllUsers(@RequestParam(name = "start", defaultValue = "0") String start,
                                      @RequestParam(name = "count", defaultValue = "10") String count) {
        return userDataHandler.getAllUserInfo(start, count);
    }

    @RequestMapping(value = "user", method = RequestMethod.POST)
    public ResponseEntity<Void> addUser(@RequestParam(name = "id", required = false) String ID,
                                        @RequestParam(name = "nickname", required = true) String nickName,
                                        @RequestParam(name = "email", required = true) String eMail,
                                        @RequestParam(name = "image", required = false) String image,
                                        @RequestParam(name = "from", required = true) String from) throws UnsupportedEncodingException, UserServiceException {
        userDataHandler.addUserInfo(ID, nickName, eMail, from, image);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "user/{ID}", method = RequestMethod.PUT)
    public ResponseEntity<UserInfo> updateArticle(@RequestBody UserInfo userInfo) {
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "userss", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUserInfo(@RequestParam(name = "email", required = true) String eMail,
                                               @RequestParam(name = "from", required = true) String from) throws UserServiceException {
        userDataHandler.removeUserinfo(eMail, from);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

}

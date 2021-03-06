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

    static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserDataHandler userDataHandle;

    @Autowired
    DataVaildator dataVaildator;

    @RequestMapping(value = "user/{ID}", method = RequestMethod.GET)
    public UserInfo getUserInfo(@PathVariable String ID) throws UserServiceException {
        return userDataHandle.getUserInfo(ID);
    }

    @RequestMapping(value = {"check/{id}", "check"}, method = RequestMethod.GET)
    public ResponseEntity<Void> existEmailOrNickName(@PathVariable(name = "id", required = false) String id,
                                                     @RequestParam(name = "email", required = false) String eMail,
                                                     @RequestParam(name = "nickname", required = false) String nickName) throws UserServiceException {

        userDataHandle.existEmailOrNickName(id, eMail, nickName);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "user", method = RequestMethod.GET)
    public UserInfo getUserInfoByEmailnFrom(@RequestParam(name = "email") String eMail,
                                            @RequestParam(name = "from") String from) throws UserServiceException {

        return userDataHandle.getUserInfo(eMail, from);
    }

    @RequestMapping(value = "user/oauth", method = RequestMethod.GET)
    public UserInfo getUserInfoByOauthIdnFrom(@RequestParam(name = "oauthId") String eMail,
                                              @RequestParam(name = "from") String from) throws UserServiceException {

        return userDataHandle.getUserInfoByOAuthIdAndFrom(eMail, from);
    }


    @RequestMapping(value = "users", method = RequestMethod.GET)
    public List<UserInfo> getAllUsers(@RequestParam(name = "start", defaultValue = "0") String start,
                                      @RequestParam(name = "count", defaultValue = "10") String count) {
        return userDataHandle.getAllUserInfo(start, count);
    }

    @RequestMapping(value = "user", method = RequestMethod.POST)
    public ResponseEntity<Void> addUser(@RequestParam(name = "id", required = false) String ID,
                                        @RequestParam(name = "nickname") String nickName,
                                        @RequestParam(name = "email") String eMail,
                                        @RequestParam(name = "image", required = false) String image,
                                        @RequestParam(name = "from") String from,
                                        @RequestParam(name = "oauthId") String oauthId) throws UnsupportedEncodingException, UserServiceException {
        userDataHandle.addUserInfo(ID, nickName, eMail, from, image, oauthId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "user/{ID}", method = RequestMethod.PATCH)
    public UserInfo updateUserInfo(@PathVariable String ID,
                                   @RequestBody UserInfo userInfo) throws UserServiceException {
        return userDataHandle.updateUserInfo(ID, userInfo);
    }

    @RequestMapping(value = "users", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUserInfo(@RequestParam(name = "email") String eMail,
                                               @RequestParam(name = "from") String from) throws UserServiceException {
        userDataHandle.removeUserinfo(eMail, from);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

package win.hellobro.web.service;

import io.netty.handler.codec.http.QueryStringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.module.service.component.http.HttpGet;
import win.hellobro.web.OAuthSite;
import win.hellobro.web.SessionKey;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.FbAccessToken;
import win.hellobro.web.component.FbApiClient;
import win.hellobro.web.component.FbUserInfo;
import win.hellobro.web.component.part.QueryStringToMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@ServiceDirectory
public class FacebookSignIn {
	private static final Logger LOG = LoggerFactory.getLogger(FacebookSignIn.class);

	@Variable private String loginUri;
	@Variable private String appId;
	@Variable private String redirectUri;

	@Variable private String accessTokenUri;
	@Variable private String clientSecretKey;
	@Variable private String userInfoUri;

	@Service("/external/user-service/get")
	private ServiceWrapper userGetter;

	@Service("oauth-uri")
	@Inbound(HttpGet.class)
	public void getOAuthUri(HttpServletRequest request) throws IOException {
		String state = UUID.randomUUID().toString();
		request.getSession().setAttribute(SessionKey.OAUTH_STATE, state);

		String oauthUri = this.loginUri +
				"client_id=" + this.appId +
				"&redirect_uri=" + this.redirectUri +
				"&state=" + state;

		HttpServletResponse response = RequestContext.getServletResponse();
		response.getWriter().write(oauthUri);
	}

	@Service("callback")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public void receiveCallback(Map<String, Object> callbackParam) throws IOException {
		HttpServletRequest request = RequestContext.getServletRequest();
		HttpServletResponse response = RequestContext.getServletResponse();
		String originalState = (String) request.getSession().getAttribute(SessionKey.OAUTH_STATE);

		if (originalState == null || !originalState.equals(callbackParam.get(SessionKey.OAUTH_STATE))) {
			LOG.error("Session state not equals. {} / {}", originalState, callbackParam.get(SessionKey.OAUTH_STATE));
			response.getWriter().write("You can't sign in.");
			return;
		}

		try {
			FbAccessToken accessToken = FbApiClient.getAccessToken(this.accessTokenUri,
					this.appId, this.redirectUri, this.clientSecretKey, (String) callbackParam.get("code"));

			FbUserInfo facebookUser = FbApiClient.getUserInfo(this.userInfoUri, accessToken.getAccessToken());

			ServiceObject serviceObject = new ServiceObject();
			serviceObject.setRequest(facebookUser.getId());

			UserInfo userInfo = this.userGetter.call(serviceObject);
			if (userInfo != null && !UserInfo.NOT_FOUND_USER.equals(userInfo)) {
				LOG.info("SIGN IN BookDream. :) {}.", userInfo.getEmail());
				request.getSession().setAttribute(SessionKey.USER_INFO, userInfo);
				response.sendRedirect("/");
			} else if (UserInfo.NOT_FOUND_USER.equals(userInfo)) {
				moveSignUpPage(facebookUser);
			} else {
				LOG.error("Login process is not normal");
				response.sendRedirect("/signin.html");
			}
		} catch (Exception e) {
			String message = "Fail to login by facebook.";
			LOG.error(message, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
	}

	private static void moveSignUpPage(FbUserInfo facebookUser) throws IOException {
		UserInfo user = new UserInfo();
		user.setId(facebookUser.getId());
		user.setEmail(facebookUser.getEmail());
		user.setNickName(facebookUser.getName());
		user.setImage(facebookUser.getPicture());
		user.setOauthSite(OAuthSite.FACEBOOK);

		HttpServletRequest request = RequestContext.getServletRequest();
		request.getSession().setAttribute(SessionKey.SIGN_UP_INFO, user);
		QueryStringEncoder signUpParam = new QueryStringEncoder("/signup.html");
		signUpParam.addParam("email", facebookUser.getEmail());
		signUpParam.addParam("name", facebookUser.getName());
		signUpParam.addParam("picture", facebookUser.getPicture());

		HttpServletResponse response = RequestContext.getServletResponse();
		response.sendRedirect(signUpParam.toString());
	}
}

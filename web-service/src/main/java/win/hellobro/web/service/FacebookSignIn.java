package win.hellobro.web.service;

import io.netty.handler.codec.http.QueryStringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceNotFoundException;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Startup;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.module.service.component.http.HttpGet;
import win.hellobro.web.OAuthSite;
import win.hellobro.web.SessionKey;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.FbAccessToken;
import win.hellobro.web.component.FbApiClient;
import win.hellobro.web.component.FbUserInfo;
import win.hellobro.web.component.part.QueryStringToMap;
import win.hellobro.web.service.external.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@ServiceDirectory
public class FacebookSignIn {
	private static final Logger LOG = LoggerFactory.getLogger(FacebookSignIn.class);

	@Variable("oauth-uri") private String loginUri;
	@Variable("oauth-uri") private String appId;
	@Variable("oauth-uri") private String redirectUri;

	@Variable("callback") private String accessTokenUri;
	@Variable("callback") private String clientSecretKey;
	@Variable("callback") private String userInfoUri;

	private UserService userService;

	@Startup
	public void init() {
		try {
			ServiceWrapper service = ServiceProvider.lookup("/external/user-service/get");
			this.userService = service.getHost();
		} catch (ServiceNotFoundException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Service("oauth-uri")
	@Inbound(HttpGet.class)
	public void getOAuthUri(HttpServletRequest request) throws IOException {
		String state = UUID.randomUUID().toString();
		request.getSession().setAttribute(SessionKey.OAUTH_STATE, state);

		String oauthUri = this.loginUri +
				"client_id=" + this.appId +
				"&redirect_uri=" + this.redirectUri +
				"&state=" + state;

		HttpServletResponse response = RequestContext.get(RequestContext.HTTP_SERVLET_RES);
		response.getWriter().write(oauthUri);
	}

	@Service("callback")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public void receiveCallback(Map<String, Object> callbackParam) throws IOException {
		HttpServletRequest request = RequestContext.get(RequestContext.HTTP_SERVLET_REQ);
		HttpServletResponse response = RequestContext.get(RequestContext.HTTP_SERVLET_RES);
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

			UserInfo userInfo = this.userService.get(facebookUser.getId());
			if (userInfo != null) {
				request.getSession().setAttribute(SessionKey.USER_INFO, userInfo);
				response.sendRedirect("/");
			} else {
				moveSignUpPage(facebookUser);
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
		user.setNickname(facebookUser.getName());
		user.setImage(facebookUser.getPicture());
		user.setOauthSte(OAuthSite.FACEBOOK);

		HttpServletRequest request = RequestContext.get(RequestContext.HTTP_SERVLET_REQ);
		request.getSession().setAttribute(SessionKey.SIGN_UP_INFO, user);
		QueryStringEncoder signUpParam = new QueryStringEncoder("/signup.html");
		signUpParam.addParam("email", facebookUser.getEmail());
		signUpParam.addParam("name", facebookUser.getName());
		signUpParam.addParam("picture", facebookUser.getPicture());

		HttpServletResponse response = RequestContext.get(RequestContext.HTTP_SERVLET_RES);
		response.sendRedirect(signUpParam.toString());
	}
}

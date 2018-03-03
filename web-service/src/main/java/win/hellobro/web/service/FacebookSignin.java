package win.hellobro.web.service;

import io.netty.handler.codec.http.QueryStringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.module.service.component.http.HttpGet;
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
public class FacebookSignin {
	private static final Logger LOG = LoggerFactory.getLogger(FacebookSignin.class);

	@Variable("oauth-uri") private String loginUri;
	@Variable("oauth-uri") private String appId;
	@Variable("oauth-uri") private String redirectUri;

	@Variable("callback") private String accessTokenUri;
	@Variable("callback") private String clientSecretKey;
	@Variable("callback") private String userInfoUri;

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
		HttpServletResponse response = RequestContext.get(RequestContext.HTTP_SERVLET_RES);
		HttpServletRequest request = RequestContext.get(RequestContext.HTTP_SERVLET_REQ);
		String originalState = (String) request.getSession().getAttribute(SessionKey.OAUTH_STATE);

		if (originalState == null || !originalState.equals(callbackParam.get(SessionKey.OAUTH_STATE))) {
			LOG.error("Session state not equals. {} / {}", originalState, callbackParam.get(SessionKey.OAUTH_STATE));
			response.getWriter().write("You can't sign in.");
			return;
		}

		try {
			FbAccessToken accessToken = FbApiClient.getAccessToken(this.accessTokenUri,
					this.appId, this.redirectUri, this.clientSecretKey, (String) callbackParam.get("code"));

			FbUserInfo userInfo = FbApiClient.getUserInfo(this.userInfoUri, accessToken.getAccessToken());
			// todo 데이터 베이스로 부터 유저 정보를 가져와서 없다면 signup 이동 / 있다면 메인페이지로 이동
//			LOG.info("move singup page. {}", userInfo.toString());
//			QueryStringEncoder queryStringEncoder = new QueryStringEncoder("/signup.html");
//			queryStringEncoder.addParam("from", "facebook");
//			queryStringEncoder.addParam("email", userInfo.getEmail());
//			queryStringEncoder.addParam("name", userInfo.getName());
//			queryStringEncoder.addParam("id", userInfo.getId());
//			queryStringEncoder.addParam("picture", userInfo.getPicture());
//			response.sendRedirect(queryStringEncoder.toString());

			UserInfo user = new UserInfo();
			user.setId(userInfo.getId());
			user.setEmail(userInfo.getEmail());
			user.setNickname(userInfo.getName());
			request.getSession().setAttribute(SessionKey.USER_INFO, user);

			LOG.info("Welcome to BOOK DREAM! {}", user.toString());
			response.sendRedirect("/");
		} catch (Exception e) {
			String message = "Can not get access token.";
			LOG.error(message, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
	}
}

package win.hellobro.web.presentation;

import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.module.service.component.http.HttpGet;
import win.hellobro.web.OAuthSite;
import win.hellobro.web.SessionRepository;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.FbApiClient;
import win.hellobro.web.component.FbUserInfo;
import win.hellobro.web.component.OAuthAccessToken;
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
	public void getOAuthUri() throws IOException {
		String state = SessionRepository.createOAuthState();
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
		String originalState = SessionRepository.getOAuthState();

		if (originalState == null || !originalState.equals(callbackParam.get("state"))) {
			LOG.error("Session state not equals. {} / {}", originalState, callbackParam.get("state"));
			response.sendRedirect("/");
			return;
		}

		try {
			OAuthAccessToken accessToken = FbApiClient.getAccessToken(this.accessTokenUri,
					this.appId, this.redirectUri, this.clientSecretKey, (String) callbackParam.get("code"));

			FbUserInfo facebookUser = FbApiClient.getUserInfo(this.userInfoUri, accessToken.getAccessToken());

			UserInfo userInfo = this.userGetter.call(facebookUser.getId(), OAuthSite.FACEBOOK);
			if (userInfo != null && !UserInfo.NOT_FOUND_USER.equals(userInfo)) {
				LOG.info("SIGN IN BookDream. :) {}.", userInfo.getEmail());

				if (StringUtil.isNullOrEmpty(userInfo.getImage())) {
					userInfo.setImage(facebookUser.getPicture());
				}

				SessionRepository.saveUserInfo(userInfo);
			} else if (UserInfo.NOT_FOUND_USER.equals(userInfo)) {
				moveSignUpPage(facebookUser);
				return;
			} else {
				LOG.error("Login process is not normal");
			}
		} catch (Exception e) {
			String message = "Fail to login by facebook.";
			LOG.error(message, e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}

		response.sendRedirect("/");
	}

	private static void moveSignUpPage(FbUserInfo facebookUser) throws IOException {
		UserInfo user = new UserInfo();
		user.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		user.setOauthId(facebookUser.getId());
		user.setEmail(facebookUser.getEmail());
		user.setNickName(facebookUser.getName());
		user.setImage(facebookUser.getPicture());
		user.setOauthSite(OAuthSite.FACEBOOK);

		SessionRepository.saveSignUpInfo(user);

		User.sendSignUpUrl(facebookUser.getEmail(), facebookUser.getName(), facebookUser.getPicture());
	}
}

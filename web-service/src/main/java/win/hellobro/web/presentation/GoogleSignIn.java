package win.hellobro.web.presentation;

import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.internal.StringUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.module.service.component.http.HttpGet;
import team.balam.exof.module.service.component.http.QueryStringToMap;
import win.hellobro.web.OAuthSite;
import win.hellobro.web.SessionRepository;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.OAuthAccessToken;
import win.hellobro.web.component.GoogleApiClient;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@ServiceDirectory
public class GoogleSignIn {
	private static final Logger LOG = LoggerFactory.getLogger(GoogleSignIn.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	@Variable private String loginUri;
	@Variable private String clientId;
	@Variable private String clientSecret;
	@Variable private String redirectUri;
	@Variable private String accessTokenUri;

	@Service("/external/user-service/get")
	private ServiceWrapper userGetter;

	@Service("oauth-uri")
	@Inbound(HttpGet.class)
	public void getOAuthUri() throws IOException {
		String state = SessionRepository.createOAuthState();
		String oauthUri = this.loginUri +
				"client_id=" + this.clientId +
				"&redirect_uri=" + this.redirectUri +
				"&state=" + state +
				"&response_type=code&scope=email profile";

		HttpServletResponse response = RequestContext.getServletResponse();
		response.getWriter().write(oauthUri);
	}

	@Service("callback")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public void receiveCallback(Map<String, String> param) throws IOException {
		LOG.info("google callback: {}", param);
		HttpServletResponse servletResponse = RequestContext.getServletResponse();

		String sessionState = SessionRepository.getOAuthState();
		if (sessionState == null || !sessionState.equals(param.get("state"))) {
			LOG.error("Session state not equals. {} / {}", sessionState, param.get("state"));
			servletResponse.sendRedirect("/");
		}

		try {
			String code = param.get("code");
			OAuthAccessToken accessToken = GoogleApiClient.getAccessToke(this.accessTokenUri, this.clientId, this.redirectUri, this.clientSecret, code);
			LOG.info("google access token: {}", accessToken);

			if (accessToken.getError() == null) {
				UserInfo googleUser = createUserInfo(accessToken.getGoogleIdToekn());

				UserInfo userInfo = this.userGetter.call(googleUser.getOauthId(), OAuthSite.GOOGLE);
				if (userInfo != null && !UserInfo.NOT_FOUND_USER.equals(userInfo)) {
					LOG.info("SIGN IN BookDream. :) {}.", userInfo.getEmail());

					if (StringUtil.isNullOrEmpty(userInfo.getImage())) {
						userInfo.setImage(googleUser.getImage());
					}

					SessionRepository.saveUserInfo(userInfo);
				} else if (UserInfo.NOT_FOUND_USER.equals(userInfo)) {
					moveSignUpPage(googleUser);
					return;
				} else {
					LOG.error("Login process is not normal");
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		servletResponse.sendRedirect("/");
	}

	@SuppressWarnings("unchecked")
	private static UserInfo createUserInfo(String idToken) throws Exception {
		String userToken = new String(Base64.getDecoder().decode(idToken.split("\\.")[1]));
		Map<String, Object> data = JSON_MAPPER.readValue(userToken, Map.class);

		UserInfo userInfo = new UserInfo();
		userInfo.setNickName((String) data.get("name"));
		userInfo.setImage((String) data.get("picture"));
		userInfo.setOauthId((String) data.get("sub"));
		userInfo.setOauthSite(OAuthSite.GOOGLE);

		if ((Boolean) data.get("email_verified")) {
			userInfo.setEmail((String) data.get("email"));
		}

		return userInfo;
	}

	private static void moveSignUpPage(UserInfo userInfo) throws IOException {
		userInfo.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		SessionRepository.saveSignUpInfo(userInfo);

		QueryStringEncoder signUpParam = new QueryStringEncoder("/signup.html");
		signUpParam.addParam("email", userInfo.getEmail());
		signUpParam.addParam("name", userInfo.getNickName());
		signUpParam.addParam("picture", userInfo.getImage());

		HttpServletResponse response = RequestContext.getServletResponse();
		response.sendRedirect(signUpParam.toString());
	}
}

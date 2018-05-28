package win.hellobro.web.presentation;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.MultiPartInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Startup;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.module.service.component.http.HttpGet;
import team.balam.exof.module.service.component.http.HttpPost;
import team.balam.exof.module.service.component.http.JsonToMap;
import win.hellobro.web.SessionRepository;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.part.QueryStringToMap;
import win.hellobro.web.service.ContentsCountVo;
import win.hellobro.web.service.ReviewService;
import win.hellobro.web.service.UserService;
import win.hellobro.web.service.external.DuplicateException;
import win.hellobro.web.service.external.RemoteUserException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ServiceDirectory
public class User {
	private static final Logger LOG = LoggerFactory.getLogger(User.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	@Variable
	private String tempDir;
	private String multipartDir;

	@Variable
	private String profileTempUrl;

	@ServiceDirectory("/service/user")
	private UserService userService;

	@ServiceDirectory("/service/review")
	private ReviewService reviewService;

	@Startup
	public void init() {
		this.multipartDir = this.tempDir + "profile/";
	}

	@Service("signUp")
	@Inbound({HttpPost.class, QueryStringToMap.class})
	public void signUp(Map<String, Object> request) throws IOException {
		HttpServletResponse response = RequestContext.getServletResponse();
		String email = (String) request.get("email");
		String nickname = (String) request.get("nickname");

		try {
			this.userService.isDuplicateEmailNickname(email, nickname);
		} catch (DuplicateException e) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
			if (e.isDuplicateEmail()) {
				response.getWriter().write("email");
			} else if (e.isDuplicateNickname()) {
				response.getWriter().write("nickname");
			}
			return;
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

		try {
			UserInfo signUpInfo = SessionRepository.getSignUpInfo();
			if (UserInfo.NOT_FOUND_USER.equals(signUpInfo)) {
				LOG.error("not found user in session. {} / {}", nickname, email);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			signUpInfo.setNickName(nickname);
			signUpInfo.setEmail(email);

			this.userService.saveUser(signUpInfo);

			LOG.info("SIGN UP Marker. :) {}.", signUpInfo.getEmail());
			SessionRepository.saveUserInfo(signUpInfo);
		} catch (RemoteUserException e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Service("profile-image")
	@Inbound(HttpPost.class)
	public void saveProfileImage(HttpServletRequest request) throws IOException, ServletException {
		String todayDir = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
		String saveDirPath = this.multipartDir + todayDir;
		File saveDir = new File(saveDirPath);

		MultipartConfigElement config = new MultipartConfigElement(saveDir.getAbsolutePath());
		MultiPartInputStream multiPart = new MultiPartInputStream(request.getInputStream(), request.getContentType(), config, new File(this.tempDir));
		MultiPartInputStream.MultiPart part = (MultiPartInputStream.MultiPart) multiPart.getPart("profileImage");

		SessionRepository.saveProfileImageFile(part.getFile().getAbsolutePath());

		HttpServletResponse response = RequestContext.getServletResponse();

		if ("y".equals(request.getParameter("direct"))) {
			try {
				UserInfo user = SessionRepository.getUserInfo();
				String imageUrl = this.userService.saveProfileImageFromSessionInfo();

				boolean isSuccess = this.userService.updateUserImage(user.getId(), imageUrl);
				if (isSuccess) {
					SessionRepository.getUserInfo().setImage(imageUrl);
				}

				response.getWriter().write(SessionRepository.getUserInfo().getImage());
			} catch (Exception e) {
				LOG.error("Fail to update profile image.", e);
			}
		} else {
			response.getWriter().write(this.profileTempUrl + todayDir + "/" + part.getFile().getName());
		}
	}

	@Service("profile")
	@Inbound(HttpGet.class)
	public void getProfile() throws IOException {
		UserInfo user = SessionRepository.getUserInfo();

		ContentsCountVo countVo = reviewService.getContentsCount(user.getId());

		Map<String, Object> info = new HashMap<>();
		info.put("email", user.getEmail());
		info.put("nickname", user.getNickName());
		info.put("image", user.getImage());
		info.put("reviewCount", countVo.getReviewCount());
		info.put("impressionCount",countVo.getImpressionCount());

		HttpServletResponse response = RequestContext.getServletResponse();
		response.setCharacterEncoding("UTF-8");
		JSON_MAPPER.writeValue(response.getWriter(), info);
	}

	@Service("update")
	@Inbound({HttpPost.class, JsonToMap.class})
	public void updateMyInfo(Map<String, Object> param) throws IOException {
		HttpServletResponse servletResponse = RequestContext.getServletResponse();
		UserInfo userInfo = SessionRepository.getUserInfo();
		String email = (String) param.get("email");
		String nickname = (String) param.get("nickname");

		if (userInfo.getEmail().equals(email) && userInfo.getNickName().equals(nickname)) {
			return;
		}

		if (!checkEmailNicknameForUpdate(servletResponse, userInfo, email, nickname)) {
			return;
		}

		LOG.info("change user info {} / {} ==> {} / {}", userInfo.getEmail(), userInfo.getNickName(), email, nickname);

		boolean isSuccess = userService.updateEmailNickname(userInfo.getId(), email, nickname);
		if (isSuccess) {
			userInfo.setEmail(email);
			userInfo.setNickName(nickname);
		} else {
			servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private boolean checkEmailNicknameForUpdate(HttpServletResponse response, UserInfo userInfo, String email, String nickname) throws IOException {
		try {
			if (userInfo.getEmail().equals(email) && !userInfo.getNickName().equals(nickname)) {
				this.userService.isDuplicateEmailNickname("@.", nickname);
			} else if (!userInfo.getEmail().equals(email) && userInfo.getNickName().equals(nickname)) {
				this.userService.isDuplicateEmailNickname(email, "@.");
			} else if (!userInfo.getEmail().equals(email) && !userInfo.getNickName().equals(nickname)) {
				this.userService.isDuplicateEmailNickname(email, nickname);
			}
		} catch (DuplicateException e) {
			if (e.isDuplicateEmail()) {
				response.sendError(HttpServletResponse.SC_CONFLICT, "email");
				return false;
			}

			if (e.isDuplicateNickname()) {
				response.sendError(HttpServletResponse.SC_CONFLICT, "nickname");
				return false;
			}
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return false;
		}

		return true;
	}

	@Service("logout")
	@Inbound(HttpPost.class)
	public void logout() throws IOException {
		UserInfo userInfo = SessionRepository.getUserInfo();
		if (userInfo != null) {
			SessionRepository.remove(SessionRepository.Type.USER_INFO);
		}

		RequestContext.getServletResponse().sendRedirect("/");
	}

	@Service("login-status")
	@Inbound(HttpGet.class)
	public void checkLogin() throws IOException {
		UserInfo userInfo = SessionRepository.getUserInfo();
		if (userInfo == UserInfo.NOT_FOUND_USER) {
			RequestContext.getServletResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	static void sendSignUpUrl(String email, String name, String picture) throws IOException {
		HttpServletResponse response = RequestContext.getServletResponse();
		response.sendRedirect("/#t=su&e=" + email + "&n=" + URLEncoder.encode(name, "UTF-8") + "&p=" + picture);
	}
}

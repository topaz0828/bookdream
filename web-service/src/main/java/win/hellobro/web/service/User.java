package win.hellobro.web.service;

import io.netty.util.internal.StringUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.MultiPartInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Startup;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.module.service.component.http.HttpGet;
import team.balam.exof.module.service.component.http.HttpPost;
import team.balam.exof.module.service.component.http.HttpPut;
import team.balam.exof.module.service.component.http.JsonToMap;
import team.balam.exof.util.StreamUtil;
import win.hellobro.web.SessionRepository;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.part.QueryStringToMap;
import win.hellobro.web.service.external.DuplicateException;
import win.hellobro.web.service.vo.BookInfo;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ServiceDirectory
public class User {
	private static final Logger LOG = LoggerFactory.getLogger(User.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	@Variable
	private String tempDir;
	private String multipartDir;

	@Variable
	private String profileImageDir;

	@Variable
	private String profileBaseUrl;

	@Variable
	private String profileTempUrl;

	@Service("/external/user-service/checkEmailAndNickname")
	private ServiceWrapper emailNicknameChecker;

	@Service("/external/user-service/save")
	private ServiceWrapper userSaver;

	@Service("/external/review-service/getReviewCount")
	private ServiceWrapper reviewCountGetter;

	@Service("/external/review-service/getImpressionCount")
	private ServiceWrapper impressionCountGetter;

	@Startup
	public void init() {
		this.multipartDir = this.tempDir + "profile/";
	}

	@Service
	@Inbound({HttpPost.class, QueryStringToMap.class})
	public void signUp(Map<String, Object> request) throws IOException {
		HttpServletResponse response = RequestContext.getServletResponse();
		String email = (String) request.get("email");
		String nickname = (String) request.get("nickname");

		if (StringUtil.isNullOrEmpty(email) || !email.contains("@") || !email.contains(".") ||
				StringUtil.isNullOrEmpty(nickname)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

		try {
			this.emailNicknameChecker.call(email, nickname);

			UserInfo signUpInfo = SessionRepository.getSignUpInfo();
			signUpInfo.setNickName(nickname);
			signUpInfo.setEmail(email);
			signUpInfo.setImage(this.saveProfileImage());

			boolean isSuccess = this.userSaver.call(signUpInfo);
			if (isSuccess) {
				LOG.info("SIGN UP Marker. :) {}.", signUpInfo.getEmail());
				SessionRepository.saveUserInfo(signUpInfo);
			} else {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			if (e.getCause() != null && e.getCause() instanceof DuplicateException) {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
				response.getWriter().write(e.getCause().getMessage());
			} else {
				LOG.error(e.getMessage(), e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}

	private String saveProfileImage() {
		File tempFile = new File(SessionRepository.getProfileImageFile());
		if (tempFile.exists()) {
			String fileName = UUID.randomUUID().toString();
			File target = new File(this.profileImageDir +  fileName);
			try (FileInputStream source = new FileInputStream(tempFile);
			     FileOutputStream profile = new FileOutputStream(target)) {
				StreamUtil.write(source, profile);

				return this.profileBaseUrl + fileName;
			} catch (IOException e) {
				LOG.error("File to save profile image file.", e);
				return "";
			} finally {
				tempFile.deleteOnExit();
			}
		}

		return "";
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
		response.getWriter().write(this.profileTempUrl + todayDir + "/" + part.getFile().getName());
	}

//	@SuppressWarnings("unchecked")
//	@Service("impression")
//	@Inbound({HttpPost.class, JsonToMap.class})
//	public void saveImpression(Map<String, Object> request) throws IOException {
//		HttpServletResponse response = RequestContext.getServletResponse();
//
//		UserInfo user = SessionRepository.getUserInfo();
//		BookInfo book = new BookInfo((Map<String, Object>) request.get("book"));
//
//		LOG.info("save impression. book[{}] impression:{}", book.getTitle(), request.get("impression"));
//
//		try {
//			boolean isSuccess = this.impressionSaver.call(user.getId(), book, request.get("impression"));
//			if (!isSuccess) {
//				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			}
//		} catch (Exception e) {
//			LOG.error("Fail to call impressionSaver.", e);
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	@Service("review")
//	@Inbound({HttpPost.class, JsonToMap.class})
//	public void saveReview(Map<String, Object> request) throws IOException {
//		UserInfo user = SessionRepository.getUserInfo();
//		BookInfo book = new BookInfo((Map<String, Object>) request.get("book"));
//		String review = (String) request.get("review");
//
//		LOG.info("save review. book[{}] review:{}", book.getTitle(), review);
//
//		HttpServletResponse response = RequestContext.getServletResponse();
//
//		try {
//			boolean isSuccess = this.reviewSaver.call(user.getId(), book, review);
//			if (!isSuccess) {
//				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			}
//		} catch (Exception e) {
//			LOG.error("Fail to save review.", e);
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@Service("contents")
//	@Inbound({HttpPut.class, JsonToMap.class})
//	public void updateReviewOrImpression(Map<String, Object> request) throws IOException {
//		HttpServletResponse response = RequestContext.getServletResponse();
//		UserInfo user = SessionRepository.getUserInfo();
//
//		if (StringUtil.isNullOrEmpty((String) request.get("contents"))) {
//			LOG.warn("contents is empty. user id: {}", user.getId());
//			return;
//		}
//
//		try {
//			boolean isSuccess = this.contentsUpdater.call(user.getId(), request.get("bookId"), request.get("type"),
//					request.get("contentsId"), request.get("contents"));
//			if (!isSuccess) {
//				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//			}
//		} catch (Exception e) {
//			LOG.error("Fail to update review or impression.", e);
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		}
//	}

	@Service("profile")
	@Inbound(HttpGet.class)
	public void getProfile() throws IOException {
		UserInfo user = SessionRepository.getUserInfo();

		int reviewCount = 0;
		int impressionCount = 0;

		try {
			reviewCount = this.reviewCountGetter.call(user.getId());
			impressionCount = this.impressionCountGetter.call(user.getId());
		} catch (Exception e) {
			LOG.error("Can not get counter info.", e);
		}

		Map<String, Object> info = new HashMap<>();
		info.put("email", user.getEmail());
		info.put("nickname", user.getNickName());
		info.put("image", user.getImage());
		info.put("reviewCount", reviewCount);
		info.put("impressionCount",impressionCount);

		HttpServletResponse response = RequestContext.getServletResponse();
		JSON_MAPPER.writeValue(response.getWriter(), info);
	}

	@Service
	@Inbound(HttpPost.class)
	public void logout() throws IOException {
		UserInfo userInfo = SessionRepository.getUserInfo();
		if (userInfo != null) {
			SessionRepository.remove(SessionRepository.Type.USER_INFO);
		}

		RequestContext.getServletResponse().sendRedirect("/signin.html");
	}
}

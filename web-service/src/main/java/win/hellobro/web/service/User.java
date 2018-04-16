package win.hellobro.web.service;

import io.netty.util.internal.StringUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceObject;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.component.http.HttpGet;
import team.balam.exof.module.service.component.http.HttpPost;
import team.balam.exof.module.service.component.http.HttpPut;
import team.balam.exof.module.service.component.http.JsonToMap;
import win.hellobro.web.SessionKey;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.part.QueryStringToMap;
import win.hellobro.web.service.vo.BookInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServiceDirectory
public class User {
	private static final Logger LOG = LoggerFactory.getLogger(User.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	@Service("/external/user-service/save")
	private ServiceWrapper userSaver;

	@Service("/external/review-service/saveImpression")
	private ServiceWrapper impressionSaver;

	@Service("/external/review-service/saveReview")
	private ServiceWrapper reviewSaver;

	@Service("/external/review-service/getReviewCount")
	private ServiceWrapper reviewCountGetter;

	@Service("/external/review-service/getImpressionCount")
	private ServiceWrapper impressionCountGetter;

	@Service("/external/review-service/updateContents")
	private ServiceWrapper contentsUpdater;

	@Service
	@Inbound({HttpPost.class, QueryStringToMap.class})
	public void signUp(Map<String, Object> request) {
		HttpServletRequest servletRequest = RequestContext.getServletRequest();
		HttpServletResponse response = RequestContext.getServletResponse();
		UserInfo signUpInfo = (UserInfo) servletRequest.getSession().getAttribute(SessionKey.SIGN_UP_INFO);
		signUpInfo.setNickName((String) request.get("nickname"));
		signUpInfo.setEmail((String) request.get("email"));

		try {
			ServiceObject serviceObject = new ServiceObject();
			serviceObject.setServiceParameter(signUpInfo);

			boolean isSuccess = this.userSaver.call(serviceObject);
			if (isSuccess) {
				LOG.info("SIGN UP Marker. :) {}.", signUpInfo.getEmail());
				servletRequest.getSession().setAttribute(SessionKey.USER_INFO, signUpInfo);
				response.sendRedirect("/");
			} else {
				response.sendRedirect("/signin.html");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Service("impression")
	@Inbound({HttpPost.class, JsonToMap.class})
	public void saveImpression(Map<String, Object> request) throws IOException {
		HttpServletRequest frontRequest = RequestContext.getServletRequest();
		HttpServletResponse response = RequestContext.getServletResponse();

		UserInfo user = (UserInfo) frontRequest.getSession().getAttribute(SessionKey.USER_INFO);
		BookInfo book = new BookInfo((Map<String, Object>) request.get("book"));

		LOG.info("save impression. book[{}] impression:{}", book.getTitle(), request.get("impression"));

		try {
			boolean isSuccess = this.impressionSaver.call(user.getId(), book, request.get("impression"));
			if (!isSuccess) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Fail to call impressionSaver.", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	@Service("review")
	@Inbound({HttpPost.class, JsonToMap.class})
	public void saveReview(Map<String, Object> request) throws IOException {
		HttpServletRequest frontRequest = RequestContext.getServletRequest();
		UserInfo user = (UserInfo) frontRequest.getSession().getAttribute(SessionKey.USER_INFO);
		BookInfo book = new BookInfo((Map<String, Object>) request.get("book"));
		String review = (String) request.get("review");

		LOG.info("save review. book[{}] review:{}", book.getTitle(), review);

		HttpServletResponse response = RequestContext.getServletResponse();

		try {
			boolean isSuccess = this.reviewSaver.call(user.getId(), book, review);
			if (!isSuccess) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Fail to save review.", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Service("contents")
	@Inbound({HttpPut.class, JsonToMap.class})
	public void updateReviewOrImpression(Map<String, Object> request) throws IOException {
		HttpServletRequest frontRequest = RequestContext.getServletRequest();
		HttpServletResponse response = RequestContext.getServletResponse();
		UserInfo user = (UserInfo) frontRequest.getSession().getAttribute(SessionKey.USER_INFO);

		if (StringUtil.isNullOrEmpty((String) request.get("contents"))) {
			LOG.warn("contents is empty. user id: {}", user.getId());
			return;
		}

		try {
			boolean isSuccess = this.contentsUpdater.call(user.getId(), request.get("bookId"), request.get("type"),
					request.get("contentsId"), request.get("contents"));
			if (!isSuccess) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOG.error("Fail to update review or impression.", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Service("profile")
	@Inbound(HttpGet.class)
	public void getProfile() throws IOException {
		HttpServletRequest frontRequest = RequestContext.getServletRequest();
		UserInfo user = (UserInfo) frontRequest.getSession().getAttribute(SessionKey.USER_INFO);

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
		HttpServletRequest servletRequest = RequestContext.getServletRequest();
		UserInfo userInfo = (UserInfo) servletRequest.getAttribute(SessionKey.USER_INFO);
		if (userInfo != null) {
			servletRequest.removeAttribute(SessionKey.USER_INFO);
		}

		RequestContext.getServletResponse().sendRedirect("/signin.html");
	}
}

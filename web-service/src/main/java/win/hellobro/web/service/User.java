package win.hellobro.web.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
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

	@Service
	@Inbound({HttpPost.class, QueryStringToMap.class})
	public void signUp(Map<String, Object> request) throws IOException {
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
				LOG.info("SIGN UP BookDream. :) {}.", signUpInfo.getEmail());
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
		UserInfo user = (UserInfo) frontRequest.getSession().getAttribute(SessionKey.USER_INFO);
		BookInfo book = new BookInfo((Map<String, Object>) request.get("book"));

		LOG.info("save impression. book[{}] impression:{}", book.getTitle(), request.get("impression"));

		boolean isSuccess = this.impressionSaver.call(new ServiceObject(user.getId(), book, request.get("impression")));
		if (!isSuccess) {
			HttpServletResponse response = RequestContext.getServletResponse();
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

		boolean isSuccess = this.reviewSaver.call(new ServiceObject(user.getId(), book, review));
		if (!isSuccess) {
			HttpServletResponse response = RequestContext.getServletResponse();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Service("profile")
	@Inbound(HttpGet.class)
	public void getProfile() throws IOException {
		HttpServletRequest frontRequest = RequestContext.getServletRequest();
		UserInfo user = (UserInfo) frontRequest.getSession().getAttribute(SessionKey.USER_INFO);
		ServiceObject serviceObject = new ServiceObject();
		serviceObject.setServiceParameter(user.getId());

		int reviewCount = this.reviewCountGetter.call(serviceObject);
		int impressionCount = this.impressionCountGetter.call(serviceObject);

		Map<String, Object> info = new HashMap<>();
		info.put("email", user.getEmail());
		info.put("nickname", user.getNickName());
		info.put("image", user.getImage());
		info.put("reviewCount", reviewCount);
		info.put("impressionCount",impressionCount);

		HttpServletResponse response = RequestContext.getServletResponse();
		JSON_MAPPER.writeValue(response.getWriter(), info);
	}
}

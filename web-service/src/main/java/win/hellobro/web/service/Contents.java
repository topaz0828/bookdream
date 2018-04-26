package win.hellobro.web.service;

import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.component.http.HttpDelete;
import team.balam.exof.module.service.component.http.HttpPost;
import team.balam.exof.module.service.component.http.HttpPut;
import team.balam.exof.module.service.component.http.JsonToMap;
import team.balam.exof.module.service.component.http.QueryStringToMap;
import win.hellobro.web.SessionRepository;
import win.hellobro.web.UserInfo;
import win.hellobro.web.service.vo.BookInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@ServiceDirectory
public class Contents {
	private static final Logger LOG = LoggerFactory.getLogger(User.class);


	@Service("/external/review-service/updateContents")
	private ServiceWrapper contentsUpdater;

	@Service("/external/review-service/saveImpression")
	private ServiceWrapper impressionSaver;

	@Service("/external/review-service/saveReview")
	private ServiceWrapper reviewSaver;

	@Service("/external/review-service/deleteContents")
	private ServiceWrapper contentsRemover;

	@SuppressWarnings("unchecked")
	@Service("impression")
	@Inbound({HttpPost.class, JsonToMap.class})
	public void saveImpression(Map<String, Object> request) throws IOException {
		HttpServletResponse response = RequestContext.getServletResponse();

		UserInfo user = SessionRepository.getUserInfo();
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
		UserInfo user = SessionRepository.getUserInfo();
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

	@Service("update")
	@Inbound({HttpPut.class, JsonToMap.class})
	public void updateReviewOrImpression(Map<String, Object> request) throws IOException {
		HttpServletResponse response = RequestContext.getServletResponse();
		UserInfo user = SessionRepository.getUserInfo();

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

	@Service("delete")
	@Inbound({HttpDelete.class, QueryStringToMap.class})
	public void deleteContents(Map<String, Object> request) throws IOException {
		String contentsId = (String) request.get("contentsId");
		String userId = SessionRepository.getUserInfo().getId();

		if (StringUtil.isNullOrEmpty(contentsId)) {
			LOG.error("### contentsId is empty.");
			return;
		}

		try {
			this.contentsRemover.call(contentsId, userId);
		} catch (Exception e) {
			LOG.error("Fail to delete contents.", e);
			RequestContext.getServletResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}

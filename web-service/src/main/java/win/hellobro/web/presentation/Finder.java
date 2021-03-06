package win.hellobro.web.presentation;

import io.netty.util.internal.StringUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.module.service.component.http.HttpGet;
import win.hellobro.web.SessionRepository;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.BookApiClient;
import win.hellobro.web.component.BookSearchResult;
import win.hellobro.web.component.part.QueryStringToMap;
import win.hellobro.web.service.ReviewService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@ServiceDirectory
public class Finder {
	private static final Logger LOG = LoggerFactory.getLogger(Finder.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	@Variable
	private String kakaoApiUri;

	@Variable
	private String kakaoApiKey;

	@ServiceDirectory("/service/review")
	private ReviewService reviewService;

	@Service("book")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public void searchBook(Map<String, Object> param) throws IOException {
		String query = (String) param.get("q");
		LOG.info("query for searching book : {}", query);

		HttpServletResponse response = RequestContext.getServletResponse();
		response.setCharacterEncoding("UTF-8");

		try {
			List<BookSearchResult> resultList = BookApiClient.search(this.kakaoApiUri, this.kakaoApiKey, query);
			if (LOG.isDebugEnabled()) {
				StringBuilder log = new StringBuilder("Search Result : \n");
				for (BookSearchResult result : resultList) {
					log.append(result.toString()).append("\n");
				}
				LOG.debug(log.toString());
			}

			JSON_MAPPER.writeValue(response.getWriter(), resultList);
		} catch (Exception e) {
			LOG.error("Fail to search book from Kakao API.", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Service("contents-list")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public void getReviewAndImpressionList(Map<String, Object> param) throws IOException {
		String isbn = (String) param.get("isbn");
		String range = (String) param.get("range");
		String pageIndex = (String) param.get("pageIndex");
		String pageSize = (String) param.get("pageSize");
		String query = (String) param.get("query");

		if (StringUtil.isNullOrEmpty(isbn)) {
			isbn = this.findIsbnFromBookApi(query);
		}

		HttpServletResponse response = RequestContext.getServletResponse();
		UserInfo user = SessionRepository.getUserInfo();
		String resultList;

		try {
			if ("my".equals(range)) {
				resultList = reviewService.getMyContents(user.getId(), isbn, pageIndex, pageSize);
			} else {
				resultList = reviewService.getAllContents(user.getId(), isbn, pageIndex, pageSize);
			}

			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(resultList != null ? resultList : "");
		} catch (Exception e) {
			LOG.error("fail to call service contentsGetter.", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Service("contents")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public void getReviewAndImpression(Map<String, Object> param) throws IOException {
		HttpServletResponse response = RequestContext.getServletResponse();
		response.setCharacterEncoding("UTF-8");

		String id = (String) param.get("id");

		try {
			Map<String, Object> reviewData = reviewService.getContentsDetail(SessionRepository.getUserInfo().getId(), id);
			response.getWriter().write(JSON_MAPPER.writeValueAsString(reviewData));
		} catch (Exception e) {
			LOG.error("fail to get contents.", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private String findIsbnFromBookApi(String query) {
		StringBuilder isbnList = new StringBuilder();

		if (!StringUtil.isNullOrEmpty(query)) {
			try {
				List<BookSearchResult> resultList = BookApiClient.search(this.kakaoApiUri, this.kakaoApiKey, query, 30);
				for (BookSearchResult book : resultList) {
					if (!StringUtil.isNullOrEmpty(book.getIsbn())) {
						isbnList.append(book.getIsbn()).append(",");
					}
				}
			} catch (Exception e) {
				LOG.error("book api is error.", e);
			}
		}

		return isbnList.toString();
	}
}

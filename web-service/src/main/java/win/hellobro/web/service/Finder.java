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
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.module.service.component.http.HttpGet;
import win.hellobro.web.SessionKey;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.BookApiClient;
import win.hellobro.web.component.BookSearchResult;
import win.hellobro.web.component.part.QueryStringToMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@ServiceDirectory
public class Finder {
	private static final Logger LOG = LoggerFactory.getLogger(Finder.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	@Variable private String kakaoApiUri;
	@Variable private String kakaoApiKey;

	@Service("/external/review-service/searchMyContentsList")
	private ServiceWrapper myContentsGetter;

	@Service("/external/review-service/searchContentsList")
	private ServiceWrapper allContentsGetter;

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

		if (!StringUtil.isNullOrEmpty(query)) {
			isbn = this.findIsbnFromBookApi(query);
		}

		String resultList;

		if ("my".equals(range)) {
			HttpServletRequest servletRequest = RequestContext.getServletRequest();
			UserInfo user = (UserInfo) servletRequest.getSession().getAttribute(SessionKey.USER_INFO);
			resultList = this.myContentsGetter.call(new ServiceObject(user.getId(), isbn, pageIndex, pageSize));
		} else {
			resultList = this.allContentsGetter.call(new ServiceObject(isbn, pageIndex, pageSize));
		}

		HttpServletResponse response = RequestContext.getServletResponse();
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(resultList != null ? resultList : "");
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

package win.hellobro.web.service;

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
import win.hellobro.web.component.BookApiClient;
import win.hellobro.web.component.BookSearchResult;
import win.hellobro.web.component.part.QueryStringToMap;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ServiceDirectory
public class Finder {
	private static final Logger LOG = LoggerFactory.getLogger(Finder.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	@Variable("book") private String kakaoApiUri;
	@Variable("book") private String kakaoApiKey;

	@Service("book")
	@Inbound({HttpGet.class, QueryStringToMap.class})
	public void searchBook(Map<String, Object> param) throws IOException {
		String query = (String) param.get("q");
		LOG.info("query for searching book : {}", query);

		HttpServletResponse response = RequestContext.get(RequestContext.HTTP_SERVLET_RES);
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
		String query = (String) param.get("query");

		//{isbn: [], range: this.range, pageIndex: this.pageIndex}
		//{query: query, range: this.range, pageIndex: this.pageIndex}

		/*
		reviewId: 'reviewId' + i,
		nickname: 'nickname' + i,
		title: 'title' + i,
		author: 'author' + i,
		updateDate: 'updateDate' + i,
		image: 'https://scontent.xx.fbcdn.net/v/t1.0-1/p100x100/15094935_1225609307512845_7310823645782503183_n.jpg?oh=697e14377cecfe09c81a08c85cd7576e&oe=5AD98CB3',
		text: '핵 감명깊은 문구다.',
		type: 'I' // I : impression, R : review
		 */

		HttpServletResponse response = RequestContext.get(RequestContext.HTTP_SERVLET_RES);

		try {
			List<BookSearchResult> resultList = BookApiClient.search(this.kakaoApiUri, this.kakaoApiKey, query, 30);
			ArrayList<String> searchIsbn = new ArrayList<>();

			for (BookSearchResult book : resultList) {
				if (!StringUtil.isNullOrEmpty(book.getIsbn())) {
					searchIsbn.add(book.getIsbn());
				}
			}

			//todo isbn을 통해서우리 db에서 책을 찾는다. 만약 검색어가 없다면 전체검색으로
			HashMap<String, Object> map = new HashMap<>();
			JSON_MAPPER.writeValue(response.getWriter(), map);
		} catch (Exception e) {
			LOG.error("Can not find book isbn.", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}

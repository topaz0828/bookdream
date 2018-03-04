package win.hellobro.web.component;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.CharsetUtil;
import org.codehaus.jackson.map.ObjectMapper;
import team.balam.exof.client.Client;
import team.balam.exof.client.DefaultClient;
import win.hellobro.web.WebService;
import win.hellobro.web.component.part.HttpsClientCodec;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookApiClient {
	private static final String API_URI;
	private static final String API_KEY;
	static {
		Map<String, String> bookApiInfo = WebService.getConfig("bookApi");
		API_URI = bookApiInfo.get("uri");
		API_KEY = "KakaoAK " + bookApiInfo.get("key");
	}

	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private BookApiClient() {

	}

	/**
	 *
	 * @param query 검색하려는 단어
	 * @return json 형태의 api 응답 (책정보 or 에러)
	 * @throws Exception api 요청 실패 시 발생
	 */
	@SuppressWarnings("unchecked")
	public static List<BookSearchResult> search(String query) throws Exception {
		URI uri = new URI(API_URI);
		QueryStringEncoder queryStringEncoder = new QueryStringEncoder(uri.getPath());
		queryStringEncoder.addParam("query", query);
		queryStringEncoder.addParam("sort", "accuracy");
		queryStringEncoder.addParam("size", "10");

		FullHttpResponse response;

		try (Client client = new DefaultClient(new HttpsClientCodec())) {
			HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, queryStringEncoder.toString());
			httpRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
			httpRequest.headers().set("Authorization", API_KEY);

			client.connect(uri.getHost(), 443);
			response = client.sendAndWait(httpRequest);
		}

		return convertResult(JSON_MAPPER.readValue(response.content().toString(CharsetUtil.UTF_8), Map.class));
	}

	@SuppressWarnings("unchecked")
	private static List<BookSearchResult> convertResult(Map<String, Object> jsonResult) {
		List<Map<String, Object>> bookInfoList = (List<Map<String, Object>>) jsonResult.get("documents");
		List<BookSearchResult> searchResults = new ArrayList<>(bookInfoList.size());

		for (Map<String, Object> book : bookInfoList) {
			BookSearchResult searchResult = new BookSearchResult();
			List<String> authors = (List<String>) book.get("authors");
			if (!authors.isEmpty()) {
				searchResult.setAuthor(authors.get(0));
			}
			String[] isbn = ((String) book.get("isbn")).split(" ");
			if (isbn.length > 0) {
				searchResult.setIsbn(((String) book.get("isbn")).split(" ")[0]);
			} else {
				searchResult.setIsbn((String) book.get("isbn"));
			}


			searchResult.setCategory((String) book.get("category"));
			searchResult.setContents((String) book.get("contents"));
			searchResult.setPublisher((String) book.get("publisher"));
			searchResult.setThumbnail((String) book.get("thumbnail"));
			searchResult.setTitle((String) book.get("title"));
			searchResult.setPurchaseUrl((String) book.get("url"));
			searchResult.setPrice((int) book.get("price"));
			searchResult.setDatetime(((String) book.get("datetime")).split("T")[0]);

			List<String> translators = (List<String>) book.get("translators");
			if (!translators.isEmpty()) {
				searchResult.setTranslators(translators.get(0));
			}

			searchResults.add(searchResult);
		}

		return searchResults;
	}
}

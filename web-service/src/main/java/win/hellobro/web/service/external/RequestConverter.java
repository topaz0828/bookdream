package win.hellobro.web.service.external;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import win.hellobro.web.presentation.vo.BookInfo;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

class RequestConverter {
	private static final Logger LOG = LoggerFactory.getLogger(RequestConverter.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private RequestConverter() {

	}

	static byte[] convert(BookInfo bookInfo) {
		HashMap<String, Object> bookData = new HashMap<>();
		bookData.put("PUB", bookInfo.getPublisher() != null ? bookInfo.getPublisher() : "");
		bookData.put("CATEGORY", bookInfo.getCategory());
		bookData.put("TITLE", bookInfo.getTitle());
		bookData.put("IMAGE", bookInfo.getThumbnail());
		bookData.put("AUTHOR", bookInfo.getAuthor());
		bookData.put("TRANSLATOR", bookInfo.getTranslator());
		bookData.put("PRICE", bookInfo.getPrice());
		bookData.put("ISBN", bookInfo.getIsbn());
		bookData.put("UPDATE_DATE", bookInfo.getDate());
		return toJson(bookData);
	}

	static byte[] convert(long bookId, String userId, String text, String type) {
		HashMap<String, Object> data = new HashMap<>();
		data.put("BOOK_ID", bookId);
		data.put("USER_ID", userId);
		data.put("TYPE", type);
		data.put("TEXT", text);
		data.put("USER_HIDDEN", "N");
		return toJson(data);
	}

	static byte[] convert(String contentsId, String userId) {
		HashMap<String, Object> data = new HashMap<>();
		data.put("USER_ID", userId);
		data.put("ID", contentsId);
		return toJson(data);
	}

	static byte[] toJson(Map<String, Object> data) {
		try {
			StringWriter writer = new StringWriter();
			JSON_MAPPER.writeValue(writer, data);
			return writer.toString().getBytes();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
}

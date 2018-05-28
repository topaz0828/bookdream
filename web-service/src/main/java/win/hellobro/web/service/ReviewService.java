package win.hellobro.web.service;

import org.codehaus.jackson.map.ObjectMapper;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import win.hellobro.web.presentation.vo.BookInfo;
import win.hellobro.web.service.external.ReviewClient;

import java.util.List;
import java.util.Map;

@ServiceDirectory
public class ReviewService {
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	@ServiceDirectory("/external/review-service")
	private ReviewClient reviewClient;

	public String getAllContents(String userId, String isbn, String pageIndex, String pageSize) {
		return reviewClient.sendSearchRequest(userId, isbn, pageIndex, pageSize, false);
	}

	public String getMyContents(String userId, String isbn, String pageIndex, String pageSize) {
		return reviewClient.sendSearchRequest(userId, isbn, pageIndex, pageSize, true);
	}

	public Map<String, Object> getContentsDetail(String userId, String reviewId) throws Exception {
		String json = reviewClient.getReviewOrImpression(reviewId);
		Map<String, Object> data = JSON_MAPPER.readValue(json, Map.class);
		String reviewUserId = (String) data.get("USER_ID");

		if (userId.equals(reviewUserId)) {
			data.put("MY", "y");
		} else {
			data.put("MY", "n");
		}

		return data;
	}

	public ContentsCountVo getContentsCount(String userId) {
		int impressionCount = reviewClient.getImpressionCount(userId);
		int reviewCount = reviewClient.getReviewCount(userId);

		return new ContentsCountVo(impressionCount, reviewCount);
	}

	public boolean saveReview(String userId, BookInfo bookInfo, String review) {
		long bookId;
		try {
			bookId = reviewClient.getBookId(bookInfo);
		} catch (Exception e) {
			return false;
		}

		return reviewClient.saveReviewAndImpression(userId, bookId, review, "R");
	}

	public boolean saveImpression(String userId, BookInfo bookInfo, List<String> impression) {
		long bookId;
		try {
			bookId = reviewClient.getBookId(bookInfo);
		} catch (Exception e) {
			return false;
		}

		for (String text : impression) {
			reviewClient.saveReviewAndImpression(userId, bookId, text, "C");
		}

		return true;
	}

	public boolean updateContents(String userId, long bookId, String type, long reviewId, String text) {
		return reviewClient.updateContents(userId, bookId, type, reviewId, text);
	}

	public void removeContents(String contentsId, String userId) {
		reviewClient.deleteContents(contentsId, userId);
	}
}

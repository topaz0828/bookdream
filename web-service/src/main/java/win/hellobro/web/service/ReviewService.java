package win.hellobro.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import win.hellobro.web.presentation.vo.BookInfo;
import win.hellobro.web.service.external.ReviewClient;

import java.util.List;

@ServiceDirectory
public class ReviewService {
	private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);

	@ServiceDirectory("/external/review-service")
	private ReviewClient reviewClient;

	public String getAllContents(String userId, String isbn, String pageIndex, String pageSize) {
		return reviewClient.sendSearchRequest(userId, isbn, pageIndex, pageSize, false);
	}

	public String getMyContents(String userId, String isbn, String pageIndex, String pageSize) {
		return reviewClient.sendSearchRequest(userId, isbn, pageIndex, pageSize, true);
	}

	public String getContentsDetail(String reviewId) {
		return reviewClient.getReviewOrImpression(reviewId);
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

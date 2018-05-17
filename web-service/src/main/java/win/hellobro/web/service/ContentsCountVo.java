package win.hellobro.web.service;

public class ContentsCountVo {
	private int impressionCount;
	private int reviewCount;

	ContentsCountVo(int impressionCount, int reviewCount) {
		this.impressionCount = impressionCount;
		this.reviewCount = reviewCount;
	}

	public int getImpressionCount() {
		return impressionCount;
	}

	public int getReviewCount() {
		return reviewCount;
	}
}

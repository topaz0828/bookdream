package win.hellobro.web.service.vo;

import java.util.Map;

public class BookInfo {
	private Map<String, Object> info;

	public BookInfo(Map<String, Object> info){
		this.info = info;
	}

	public String getAuthor() {
		return (String) this.info.get("author");
	}

	public String getTitle() {
		return (String) this.info.get("title");
	}

	public String getIsbn() {
		return (String) this.info.get("isbn");
	}

	public String getCategory() {
		return (String) this.info.get("category");
	}

	public String getPublisher() {
		return (String) this.info.get("publisher");
	}

	public String getThumbnail() {
		return (String) this.info.get("thumbnail");
	}

	public String getTranslator() {
		return (String) this.info.get("translators");
	}

	public String getPurchaseUrl() {
		return (String) this.info.get("purchaseUrl");
	}

	public int getPrice() {
		return (int) this.info.get("price");
	}

	public String getDate() {
		return (String) this.info.get("datetime");
	}
}
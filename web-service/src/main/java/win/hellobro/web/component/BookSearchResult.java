package win.hellobro.web.component;

public class BookSearchResult {
	private String author;
	private String category;
	private String contents; // 내용 요약
	private String isbn;
	private String publisher;
	private String title;
	private String thumbnail;
	private String translators;
	private String purchaseUrl;
	private int price;
	private String datetime; // yyyy-MM-DD

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getTranslators() {
		return translators;
	}

	public void setTranslators(String translators) {
		this.translators = translators;
	}

	public String getPurchaseUrl() {
		return purchaseUrl;
	}

	public void setPurchaseUrl(String purchaseUrl) {
		this.purchaseUrl = purchaseUrl;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getPrice() {
		return price;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getDatetime() {
		return datetime;
	}

	@Override
	public String toString() {
		return "BookSearchResult{" +
				"author='" + author + '\'' +
				", category='" + category + '\'' +
				", contents='" + contents + '\'' +
				", isbn='" + isbn + '\'' +
				", publisher='" + publisher + '\'' +
				", title='" + title + '\'' +
				", thumbnail='" + thumbnail + '\'' +
				", translators='" + translators + '\'' +
				", purchaseUrl='" + purchaseUrl + '\'' +
				", price='" + price + '\'' +
				", datetime=" + datetime +
				'}';
	}
}

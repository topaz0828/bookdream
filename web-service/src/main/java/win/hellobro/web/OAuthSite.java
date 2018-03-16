package win.hellobro.web;

public enum OAuthSite {
	FACEBOOK("facebook");

	String value;

	OAuthSite(String value) {
		this.value = value;
	}

	public String val() {
		return this.value;
	}
}

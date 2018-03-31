package win.hellobro.web;

import java.util.HashMap;
import java.util.Map;

public enum OAuthSite {
	FACEBOOK("facebook");

	private static final Map<String, OAuthSite> MAP = new HashMap<>();
	private String value;

	static {
		for (OAuthSite site : OAuthSite.values()) {
			MAP.put(site.val(), site);
		}
	}

	OAuthSite(String value) {
		this.value = value;
	}

	public String val() {
		return this.value;
	}

	public static OAuthSite get(String site) {
		return MAP.get(site);
	}
}

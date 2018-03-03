package win.hellobro.web.component;

import java.util.Map;

/**
 * {"access_token":"aaaaa",
 * "token_type":"bearer",
 * "expires_in":5183435}
 */
public class FbAccessToken {
	private Map<String, Object> info;

	FbAccessToken(Map<String, Object> info) {
		this.info = info;
	}

	public String getAccessToken() {
		return (String) this.info.get("access_token");
	}

	public String getTokenType() {
		return (String) this.info.get("token_type");
	}

	public int getExpiresIn() {
		return (int) this.info.get("expires_in");
	}
}

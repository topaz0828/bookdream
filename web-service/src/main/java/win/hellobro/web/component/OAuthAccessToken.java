package win.hellobro.web.component;

import java.util.Map;

public class OAuthAccessToken {
	private Map<String, Object> info;

	OAuthAccessToken(Map<String, Object> info) {
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

	public String getError() {
		return (String) info.get("error");
	}

	public String getGoogleIdToekn() {
		return (String) this.info.get("id_token");
	}

	@Override
	public String toString() {
		return this.info.toString();
	}
}

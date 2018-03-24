package win.hellobro.web.component;

import java.io.Serializable;
import java.util.Map;

public class FbUserInfo implements Serializable {
	private Map<String, Object> userInfo;

	FbUserInfo(Map<String, Object> userInfo) {
		this.userInfo = userInfo;
	}

	public String getId() {
		return (String) this.userInfo.get("id");
	}

	public String getName() {
		return (String) this.userInfo.get("name");
	}

	public String getEmail() {
		return (String) this.userInfo.get("email");
	}

	public String getPicture() {
		Map<String, Object> picture = (Map<String, Object>) this.userInfo.get("picture");
		if (picture != null) {
			Map<String, Object> data = (Map<String, Object>) picture.get("data");
			return (String) data.get("url");
		}

		return null;
	}

	@Override
	public String toString() {
		return "FbUserInfo{" +
				"userInfo=" + userInfo +
				'}';
	}
}

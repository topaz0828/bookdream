package win.hellobro.web;

import java.io.Serializable;

public class UserInfo implements Serializable {
	public static final UserInfo NOT_FOUND_USER = new UserInfo();
	static {
		NOT_FOUND_USER.id = "";
		NOT_FOUND_USER.nickName = "";
		NOT_FOUND_USER.email = "";
	}

	private String id;
	private String nickName;
	private String email;
	private String oauthSite;
	private String oauthId;
	private String image;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getNickName() {
		return nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public OAuthSite getOauthSite() {
		return OAuthSite.get(oauthSite);
	}

	public void setOauthSite(OAuthSite oauthSite) {
		this.oauthSite = oauthSite.val();
	}

	public void setOauthId(String oauthId) {
		this.oauthId = oauthId;
	}

	public String getOauthId() {
		return oauthId;
	}

	public void setOauthSite(String oauthSite) {
		if (OAuthSite.get(oauthSite) == null) {
			throw new IllegalArgumentException("oauth site is wrong. " + oauthSite);
		}
		this.oauthSite = oauthSite;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "UserInfo{" +
				"id='" + id + '\'' +
				", nickname='" + nickName + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}

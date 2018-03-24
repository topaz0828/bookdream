package win.hellobro.web;

import java.io.Serializable;

public class UserInfo implements Serializable {
	private String id;
	private String nickname;
	private String email;
	private OAuthSite oauthSite;
	private String image;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public OAuthSite getOauthSite() {
		return oauthSite;
	}

	public void setOauthSte(OAuthSite oauthSite) {
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
				", nickname='" + nickname + '\'' +
				", email='" + email + '\'' +
				'}';
	}
}

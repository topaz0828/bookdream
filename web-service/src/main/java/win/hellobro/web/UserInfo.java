package win.hellobro.web;

import java.io.Serializable;

public class UserInfo implements Serializable {
	public static final UserInfo NOT_FOUND_USER = new UserInfo();

	private String id;
	private String nickName;
	private String email;
	private OAuthSite oauthSite;
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
		return oauthSite;
	}

	public void setOauthSite(OAuthSite oauthSite) {
		this.oauthSite = oauthSite;
	}

	public void setOauthSite(String oauthSite) {
		this.oauthSite = OAuthSite.get(oauthSite);
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

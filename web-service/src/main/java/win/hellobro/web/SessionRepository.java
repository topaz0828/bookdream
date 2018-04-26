package win.hellobro.web;

import team.balam.exof.module.listener.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class SessionRepository {
	public enum Type {
		OAUTH_STATE,
		SIGN_UP_INFO,
		PROFILE_IMAGE_FILE_NAME,
		USER_INFO
	}

	public static String createOAuthState() {
		String state = UUID.randomUUID().toString();
		HttpServletRequest request = RequestContext.getServletRequest();
		request.getSession().setAttribute(Type.OAUTH_STATE.name(), state);

		return state;
	}

	public static String getOAuthState() {
		HttpServletRequest request = RequestContext.getServletRequest();
		return (String) request.getSession().getAttribute(Type.OAUTH_STATE.name());
	}

	public static UserInfo getSignUpInfo() {
		HttpServletRequest request = RequestContext.getServletRequest();
		UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Type.SIGN_UP_INFO.name());
		return userInfo != null ? userInfo : UserInfo.NOT_FOUND_USER;
	}

	public static void saveSignUpInfo(UserInfo userInfo) {
		HttpServletRequest request = RequestContext.getServletRequest();
		request.getSession().setAttribute(Type.SIGN_UP_INFO.name(), userInfo);
	}

	public static UserInfo getUserInfo() {
		HttpServletRequest request = RequestContext.getServletRequest();
		UserInfo userInfo = (UserInfo) request.getSession().getAttribute(Type.USER_INFO.name());
		return userInfo != null ? userInfo : UserInfo.NOT_FOUND_USER;
	}

	public static void saveUserInfo(UserInfo userInfo) {
		HttpServletRequest request = RequestContext.getServletRequest();
		request.getSession().setAttribute(Type.USER_INFO.name(), userInfo);
	}

	public static void saveProfileImageFile(String fileName) {
		HttpServletRequest request = RequestContext.getServletRequest();
		request.getSession().setAttribute(Type.PROFILE_IMAGE_FILE_NAME.name(), fileName);
	}

	public static String getProfileImageFile() {
		HttpServletRequest request = RequestContext.getServletRequest();
		return (String) request.getSession().getAttribute(Type.PROFILE_IMAGE_FILE_NAME.name());
	}

	public static void remove(Type type) {
		HttpServletRequest request = RequestContext.getServletRequest();
		request.removeAttribute(type.name());
	}
}

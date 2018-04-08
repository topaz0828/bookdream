package win.hellobro.web;

import team.balam.exof.module.was.ServicePathExtractor;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

public class RequestFilter implements ServicePathExtractor {
	//로그인 상태가 아니라도 접근할 수 있는 페이지
	private static Set<String> notLoginPath = new HashSet<>();

	public RequestFilter() {
		notLoginPath.add("/user/signUp");
		notLoginPath.add("/signin/facebook/oauth-uri");
		notLoginPath.add("/signin/facebook/callback");
	}

	@Override
	public String extract(HttpServletRequest httpServletRequest) {
		String requestPath = httpServletRequest.getPathInfo();
		if (!notLoginPath.contains(requestPath)) {
			UserInfo info = (UserInfo) httpServletRequest.getSession().getAttribute(SessionKey.USER_INFO);
			if (info == null) {
				return "/error/send-unauthorized";
			}
		}

		return requestPath;
	}
}

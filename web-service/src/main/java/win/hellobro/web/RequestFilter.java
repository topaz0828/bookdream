package win.hellobro.web;

import team.balam.exof.module.was.ServicePathExtractor;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RequestFilter implements ServicePathExtractor, Filter {
	//로그인 상태가 아니라도 접근할 수 있는 페이지
	private static Set<String> notLoginPath = new HashSet<>();

	public RequestFilter() {
		notLoginPath.add("/user/profile-image");
		notLoginPath.add("/user/signUp");
		notLoginPath.add("/signin/facebook/oauth-uri");
		notLoginPath.add("/signin/facebook/callback");
	}

	@Override
	public String extract(HttpServletRequest httpServletRequest) {
		String uri = httpServletRequest.getRequestURI();
		String requestPath = httpServletRequest.getPathInfo();

		if (uri.contains("/mypage")) {
			return "/non-api/send-index-page";
		} else if (uri.contains("/api") && !notLoginPath.contains(requestPath)) {
			UserInfo info = SessionRepository.getUserInfo();
			if (UserInfo.NOT_FOUND_USER.equals(info)) {
				return "/non-api/send-unauthorized";
			}
		}

		return requestPath;
	}

	@Override
	public void init(FilterConfig filterConfig) {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;


		if ("http".equalsIgnoreCase(req.getScheme()) &&
				!"localhost".equalsIgnoreCase(new java.net.URL(req.getRequestURL().toString()).getHost())) {
			res.sendRedirect("https://book.hellobro.win");
			return;
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}
}

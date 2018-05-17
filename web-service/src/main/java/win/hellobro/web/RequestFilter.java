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

public class RequestFilter implements ServicePathExtractor, Filter {
	@Override
	public String extract(HttpServletRequest httpServletRequest) {
		String uri = httpServletRequest.getRequestURI();
		String requestPath = httpServletRequest.getPathInfo();

		if (uri.contains("/mypage")) {
			UserInfo info = SessionRepository.getUserInfo();
			if (UserInfo.NOT_FOUND_USER.equals(info)) {
				return "/non-api/move-main";
			} else {
				return "/non-api/move-my-page";
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

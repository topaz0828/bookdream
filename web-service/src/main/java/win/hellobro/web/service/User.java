package win.hellobro.web.service;

import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.component.http.HttpGet;
import team.balam.exof.module.service.component.http.HttpPost;
import team.balam.exof.module.service.component.http.JsonToMap;
import win.hellobro.web.SessionKey;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.part.QueryStringToMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServiceDirectory
public class User {
	private static final Logger LOG = LoggerFactory.getLogger(User.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	@Service
	@Inbound({HttpPost.class, QueryStringToMap.class})
	public void signup(Map<String, Object> request) throws IOException {
		//todo 받은 데이터를 저장한 후 메인 페이지로이동한다.

		LOG.info("BookDream sign up is succeed. :) {}.", request.get("email"));
		HttpServletResponse response = RequestContext.get(RequestContext.HTTP_SERVLET_RES);
		response.sendRedirect("/");
	}

	@Service("impression")
	@Inbound({HttpPost.class, JsonToMap.class})
	public void saveImpression(Map<String, Object> request) throws IOException {
		//todo 받은 데이터 저장
	}

	@Service("review")
	@Inbound({HttpPost.class, JsonToMap.class})
	public void saveReview(Map<String, Object> request) throws IOException {
		//todo 받은 데이터 저장
	}

	@Service("profile")
	@Inbound(HttpGet.class)
	public void getProfile() throws IOException {
//		HttpServletRequest request = RequestContext.get(RequestContext.HTTP_SERVLET_REQ);
//		UserInfo user = (UserInfo) request.getSession().getAttribute(SessionKey.USER_INFO);
//		String id = user.getId();

		//todo id 를 통해서 정보를 가져온다.
		Map<String, Object> info = new HashMap<>();
		info.put("email", "nerobian@naver.com");
		info.put("nickname", "KNero");
		info.put("image", "https://scontent.xx.fbcdn.net/v/t1.0-1/p100x100/15094935_1225609307512845_7310823645782503183_n.jpg?oh=697e14377cecfe09c81a08c85cd7576e&oe=5AD98CB3");
		info.put("reviewCount", 15);
		info.put("impressionCount",12);

		HttpServletResponse response = RequestContext.get(RequestContext.HTTP_SERVLET_RES);
		JSON_MAPPER.writeValue(response.getWriter(), info);
	}
}

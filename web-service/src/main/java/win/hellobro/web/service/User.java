package win.hellobro.web.service;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringEncoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.client.Client;
import team.balam.exof.client.DefaultClient;
import team.balam.exof.client.component.HttpClientCodec;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.ServiceNotFoundException;
import team.balam.exof.module.service.ServiceProvider;
import team.balam.exof.module.service.ServiceWrapper;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Startup;
import team.balam.exof.module.service.component.http.HttpGet;
import team.balam.exof.module.service.component.http.HttpPost;
import team.balam.exof.module.service.component.http.JsonToMap;
import win.hellobro.web.SessionKey;
import win.hellobro.web.UserInfo;
import win.hellobro.web.component.part.QueryStringToMap;
import win.hellobro.web.service.external.UserService;
import win.hellobro.web.service.external.UserServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ServiceDirectory
public class User {
	private static final Logger LOG = LoggerFactory.getLogger(User.class);
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private UserService userService;

	@Startup
	public void init() {
		try {
			ServiceWrapper service = ServiceProvider.lookup("/external/user-service/get");
			this.userService = service.getHost();
		} catch (ServiceNotFoundException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Service
	@Inbound({HttpPost.class, QueryStringToMap.class})
	public void signUp(Map<String, Object> request) throws IOException {
		HttpServletRequest servletRequest = RequestContext.get(RequestContext.HTTP_SERVLET_REQ);
		HttpServletResponse response = RequestContext.get(RequestContext.HTTP_SERVLET_RES);
		UserInfo signUpInfo = (UserInfo) servletRequest.getSession().getAttribute(SessionKey.SIGN_UP_INFO);
		signUpInfo.setNickname((String) request.get("nickname"));
		signUpInfo.setEmail((String) request.get("email"));

		try {
			this.userService.save(signUpInfo);

			LOG.info("sign up BookDream. :) {}.", signUpInfo.getEmail());
			response.sendRedirect("/");
		} catch (UserServiceException e) {
			LOG.error(e.getMessage(), e);
			response.sendRedirect("/signin.html");
		}
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
		HttpServletRequest frontRequest = RequestContext.get(RequestContext.HTTP_SERVLET_REQ);
		UserInfo user = (UserInfo) frontRequest.getSession().getAttribute(SessionKey.USER_INFO);

//		try (Client client = new DefaultClient(new HttpClientCodec())) {
//			QueryStringEncoder queryStringEncoder = new QueryStringEncoder("/test/receiveHttp");
//			queryStringEncoder.addParam("id", user);
//			URI uri = new URI("/test/receiveHttp?message=response");
//			HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
//
//			client.setConnectTimeout(3000);
//			client.connect("192.168.1.158", 9004);
//			client.sendAndWait(request);
//		} catch (Exception e) {
//
//		}

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

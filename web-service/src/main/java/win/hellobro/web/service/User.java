package win.hellobro.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Inbound;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.component.http.HttpPost;
import team.balam.exof.module.service.component.http.JsonToMap;
import win.hellobro.web.component.part.QueryStringToMap;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@ServiceDirectory
public class User {
	private static final Logger LOG = LoggerFactory.getLogger(User.class);

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
}

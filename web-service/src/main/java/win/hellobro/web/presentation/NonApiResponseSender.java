package win.hellobro.web.presentation;

import org.eclipse.jetty.http.HttpHeaders;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.util.StreamUtil;
import win.hellobro.web.SessionRepository;
import win.hellobro.web.UserInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

@ServiceDirectory
public class NonApiResponseSender {
	@Service("send-unauthorized")
	public void sendUnauthorized() throws IOException {
		RequestContext.getServletResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Service("move-main")
	public void moveMain() throws IOException {
		RequestContext.getServletResponse().sendRedirect("/");
	}

	@Service("move-my-page")
	public void refreshIndex() throws IOException {
		HttpServletResponse response = RequestContext.getServletResponse();
		response.setHeader(HttpHeaders.CONTENT_TYPE, "text/html");

		try (FileInputStream in = new FileInputStream(new File("./webapp/index.html"));
		     OutputStream out = response.getOutputStream()) {
			StreamUtil.write(in, out);
		}
	}
}

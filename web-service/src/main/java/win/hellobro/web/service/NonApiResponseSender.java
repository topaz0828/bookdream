package win.hellobro.web.service;

import org.eclipse.jetty.http.HttpHeaders;
import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.util.StreamUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

@ServiceDirectory
public class NonApiResponseSender {
	@Service("send-unauthorized")
	public void redirectSignIn() throws IOException {
		RequestContext.getServletResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Service("send-index-page")
	public void refreshIndex() throws IOException {
		File file = new File("./webapp/index.html");
		HttpServletResponse response = RequestContext.getServletResponse();
		response.setHeader(HttpHeaders.CONTENT_TYPE, "text/html");

		try (FileInputStream in = new FileInputStream(file);
		     OutputStream out = response.getOutputStream()) {
			StreamUtil.write(in, out);
		}
	}
}

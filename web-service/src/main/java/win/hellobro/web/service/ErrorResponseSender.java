package win.hellobro.web.service;

import team.balam.exof.module.listener.RequestContext;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ServiceDirectory
public class ErrorResponseSender {
	@Service("send-unauthorized")
	public void redirectSignIn() throws IOException {
		RequestContext.getServletResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}
}

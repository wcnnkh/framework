package scw.servlet.view;

import java.io.IOException;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;

public class HttpCode implements View {
	private static Logger logger = LoggerFactory.getLogger(HttpCode.class);

	private int status;
	private String msg;

	public HttpCode(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	public void render(Request request, Response response) throws IOException {
		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		if (response.getRequest().isDebug()) {
			StringBuilder sb = new StringBuilder();
			sb.append("servletPath=").append(request.getServletPath());
			sb.append(",method=").append(request.getMethod());
			sb.append(",status=");
			sb.append(status);
			sb.append(",msg=");
			sb.append(msg);
			logger.debug(sb.toString());
		}
		response.sendError(status, msg);
	}
}

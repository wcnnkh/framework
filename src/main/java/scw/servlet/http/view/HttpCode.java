package scw.servlet.http.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.core.logger.DebugLogger;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;
import scw.servlet.View;

public class HttpCode implements View {
	private int status;
	private String msg;

	public HttpCode(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	public void render(Request request, Response response) throws Exception {
		if (!ServletUtils.isHttpServlet(request, response)) {
			return;
		}

		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if (response instanceof DebugLogger) {
			if (((DebugLogger) response).isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("servletPath=").append(httpServletRequest.getServletPath());
				sb.append(",method=").append(httpServletRequest.getMethod());
				sb.append(",status=");
				sb.append(status);
				sb.append(",msg=");
				sb.append(msg);
				((DebugLogger) response).debug(sb.toString());
			}
		}
		httpServletResponse.sendError(status, msg);
	}
}

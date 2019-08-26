package scw.servlet.http.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		if (response.isLogEnabled()) {
			response.log("servletPath={},method={},status={},msg={}", httpServletRequest.getServletPath(),
					httpServletRequest.getMethod(), status, msg);
		}
		httpServletResponse.sendError(status, msg);
	}
}

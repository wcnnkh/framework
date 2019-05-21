package scw.servlet.view;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import scw.core.logger.DebugLogger;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;
import scw.servlet.View;

public class Jsp extends HashMap<String, Object> implements View {
	private static final long serialVersionUID = 1L;
	private String page;

	public Jsp(String page) {
		this.page = page;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void render(Request request, Response response) throws Exception {
		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		for (Entry<String, Object> entry : entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}

		String page = getPage();
		if (page == null && request instanceof HttpServletRequest) {
			page = ((HttpServletRequest) request).getServletPath();
		}
		
		if (response instanceof DebugLogger) {
			if (((DebugLogger) response).isDebugEnabled()) {
				((DebugLogger) response).debug(page);
			}
		}

		ServletUtils.jsp(request, response, page);
	}
}

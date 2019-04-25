package scw.servlet.view;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;

public class Jsp extends HashMap<String, Object> implements View {
	private static Logger logger = LoggerFactory.getLogger(Jsp.class);
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

	public void render(Request request, Response response) throws IOException {
		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		if (getPage() == null) {
			setPage(response.getRequest().getServletPath());
		}

		for (Entry<String, Object> entry : entrySet()) {
			response.getRequest().setAttribute(entry.getKey(), entry.getValue());
		}

		String page = getPage();
		if (page == null) {
			page = response.getRequest().getServletPath() + ".jsp";
		}

		try {
			jsp(response.getRequest(), response, getPage());
		} catch (ServletException e) {
			throw new RuntimeException(e);
		} finally {
			if (response.getRequest().isDebug()) {
				logger.debug(page);
			}
		}
	}

	public static void jsp(Request request, Response response, String page) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		dispatcher.forward(request, response);
	}
}

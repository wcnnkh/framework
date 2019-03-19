package scw.servlet.view;

import java.io.IOException;

import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;

public class Redirect implements View {
	private static Logger logger = LoggerFactory.getLogger(Redirect.class);

	private String url;

	public Redirect(String url) {
		this.url = url;
	}

	public void render(Request request, Response response) throws IOException {
		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		if (response.getRequest().isDebug()) {
			logger.debug(url);
		}

		response.sendRedirect(url);
	}
}

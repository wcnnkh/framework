package scw.servlet.http.view;

import javax.servlet.http.HttpServletResponse;

import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;

public class Redirect implements View {
	private String url;

	public Redirect(String url) {
		this.url = url;
	}

	public void render(Request request, Response response) throws Exception {
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		if (response.isDebugEnabled()) {
			response.debug("redirect:{}", url);
		}
		httpServletResponse.sendRedirect(url);
	}
}

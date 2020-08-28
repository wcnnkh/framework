package scw.mvc.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.mvc.HttpChannel;
import scw.mvc.page.AbstractPage;
import scw.servlet.ServletUtils;
import scw.servlet.http.ServletServerHttpRequest;
import scw.servlet.http.ServletServerHttpResponse;

public class Jsp extends AbstractPage {
	private static final long serialVersionUID = 1L;

	protected Jsp() {
		super(null);
	};

	public Jsp(String page) {
		super(page);
	}

	public void render(HttpChannel httpChannel) throws IOException {
		HttpServletRequest request = ((ServletServerHttpRequest) httpChannel.getRequest()).getHttpServletRequest();
		HttpServletResponse response = ((ServletServerHttpResponse) httpChannel.getResponse()).getHttpServletResponse();

		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> attributeMap = (Map<String, Object>) clone();
		Enumeration<String> enumeration = request.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			attributeMap.remove(enumeration.nextElement());
		}

		for (java.util.Map.Entry<String, Object> entry : attributeMap.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}

		if (httpChannel.isLogEnabled()) {
			httpChannel.log("jsp:{}", getPage());
		}
		try {
			ServletUtils.jsp(request, response, getPage());
		} catch (ServletException e) {
			httpChannel.getLogger().error(e, httpChannel.toString());
		}
	}
}

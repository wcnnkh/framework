package scw.mvc.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.lang.NotSupportedException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.HttpChannel;
import scw.mvc.page.AbstractPage;
import scw.servlet.ServletUtils;
import scw.servlet.http.ServletServerHttpRequest;
import scw.servlet.http.ServletServerHttpResponse;

public class Jsp extends AbstractPage {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtils.getLogger(Jsp.class);

	protected Jsp() {
		super(null);
	};

	public Jsp(String page) {
		super(page);
	}

	public void render(HttpChannel httpChannel) throws IOException {
		ServletServerHttpRequest request = ServletUtils.getServletServerHttpRequest(httpChannel.getRequest());
		ServletServerHttpResponse response = ServletUtils.getServletServerHttpResponse(httpChannel.getResponse());
		if (request == null || response == null) {
			throw new NotSupportedException(httpChannel.toString());
		}

		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		HttpServletRequest httpServletRequest = request.getHttpServletRequest();
		HttpServletResponse httpServletResponse = response.getHttpServletResponse();
		@SuppressWarnings("unchecked")
		Map<String, Object> attributeMap = (Map<String, Object>) clone();
		Enumeration<String> enumeration = httpServletRequest.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			attributeMap.remove(enumeration.nextElement());
		}

		for (java.util.Map.Entry<String, Object> entry : attributeMap.entrySet()) {
			httpServletRequest.setAttribute(entry.getKey(), entry.getValue());
		}

		if (logger.isDebugEnabled()) {
			logger.debug("jsp:{}", getPage());
		}
		try {
			ServletUtils.jsp(httpServletRequest, httpServletResponse, getPage());
		} catch (ServletException e) {
			logger.error(e, httpChannel.toString());
		}
	}
}

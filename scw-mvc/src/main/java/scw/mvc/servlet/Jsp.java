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
import scw.util.XUtils;

public class Jsp extends AbstractPage {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtils.getLogger(Jsp.class);

	protected Jsp() {
		super(null);
	};

	public Jsp(String page) {
		super(page);
	}
	
	@Override
	protected void renderInternal(HttpChannel httpChannel) throws IOException {
		HttpServletRequest request = XUtils.getTarget(httpChannel, HttpServletRequest.class);
		HttpServletResponse response = XUtils.getTarget(httpChannel, HttpServletResponse.class);
		if (request == null || response == null) {
			throw new NotSupportedException(httpChannel.toString());
		}

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
		
		try {
			ServletUtils.jsp(request, response, getPage());
		} catch (ServletException e) {
			logger.error(e, httpChannel.toString());
		}
	}
}

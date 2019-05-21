package scw.servlet.page;

import java.util.Enumeration;
import java.util.Map;

import scw.core.logger.DebugLogger;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;

public class Jsp extends AbstractPage {
	private static final long serialVersionUID = 1L;

	protected Jsp() {
		super(null);
	};

	public Jsp(String page) {
		super(page);
	}

	@SuppressWarnings("unchecked")
	public void render(Request request, Response response) throws Exception {
		if (response.getContentType() == null) {
			response.setContentType("text/html;charset=" + response.getCharacterEncoding());
		}

		Map<String, Object> attributeMap = (Map<String, Object>) clone();
		Enumeration<String> enumeration = request.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			attributeMap.remove(enumeration.nextElement());
		}

		for (Entry<String, Object> entry : attributeMap.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}

		if (response instanceof DebugLogger) {
			if (((DebugLogger) response).isDebugEnabled()) {
				((DebugLogger) response).debug("jsp：" + getPage());
			}
		}
		
		ServletUtils.jsp(request, response, getPage());
	}
}

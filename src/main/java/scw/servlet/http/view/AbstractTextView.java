package scw.servlet.http.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import scw.core.ConvertToString;
import scw.core.logger.DebugLogger;
import scw.core.net.http.ContentType;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;
import scw.servlet.View;

public abstract class AbstractTextView implements View, ConvertToString {
	private Map<String, String> responseProperties;

	public void addResponseHeader(String key, String value) {
		if (responseProperties == null) {
			responseProperties = new HashMap<String, String>(8);
		}
		responseProperties.put(key, value);
	}
	
	public void render(Request request, Response response) throws Exception {
		if (!ServletUtils.isHttpServlet(request, response)) {
			return;
		}

		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if (responseProperties != null) {
			for (Entry<String, String> entry : responseProperties.entrySet()) {
				httpServletResponse.setHeader(entry.getKey(), entry.getValue());
			}
		}

		if (response.getContentType() == null) {
			response.setContentType(ContentType.TEXT_HTML);
		}

		String content = convertToString();
		if (response instanceof DebugLogger) {
			if (((DebugLogger) response).isDebugEnabled()) {
				((DebugLogger) response).debug(content);
			}
		}
		httpServletResponse.getWriter().write(content);
	}
}

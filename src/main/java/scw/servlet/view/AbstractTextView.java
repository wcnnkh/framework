package scw.servlet.view;

import scw.core.ConvertToString;
import scw.core.logger.DebugLogger;
import scw.core.net.http.ContentType;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;

public abstract class AbstractTextView implements View, ConvertToString {

	public void render(Request request, Response response) throws Exception {
		if (response.getContentType() == null) {
			response.setContentType(ContentType.TEXT_HTML);
		}

		String content = convertToString();
		if (response instanceof DebugLogger) {
			if (((DebugLogger) response).isDebugEnabled()) {
				((DebugLogger) response).debug(content);
			}
		}
		response.getWriter().write(content);
	}
}

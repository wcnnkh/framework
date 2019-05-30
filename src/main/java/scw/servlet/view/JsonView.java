package scw.servlet.view;

import java.util.HashMap;

import scw.core.ConvertToString;
import scw.core.logger.DebugLogger;
import scw.core.net.http.ContentType;
import scw.json.JSONUtils;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.View;

public class JsonView extends HashMap<String, Object> implements View {
	private static final long serialVersionUID = 1L;

	public JsonView() {
		super();
	};

	public JsonView(int initialCapacity) {
		super(initialCapacity);
	}

	public JsonView(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public void render(Request request, Response response) throws Exception {
		if (response.getContentType() == null) {
			response.setContentType(ContentType.APPLICATION_JSON);
		}

		String content;
		if (this instanceof ConvertToString) {
			content = ((ConvertToString) this).convertToString();
		} else {
			content = JSONUtils.toJSONString(this);
		}

		if (response instanceof DebugLogger) {
			if (((DebugLogger) response).isDebugEnabled()) {
				((DebugLogger) response).debug(content);
			}
		}
		response.getWriter().write(content);
	}
}

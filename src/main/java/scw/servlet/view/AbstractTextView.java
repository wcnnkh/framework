package scw.servlet.view;

import scw.core.utils.StringUtils;
import scw.net.ContentType;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.Text;
import scw.servlet.View;

public abstract class AbstractTextView implements View, Text {

	public void render(Request request, Response response) throws Exception {
		String content = getTextContent();
		String contentType = getTextContentType();
		if (StringUtils.isEmpty(contentType)) {
			if (StringUtils.isEmpty(response.getContentType())) {
				response.setContentType(ContentType.TEXT_HTML);
			}
		} else {
			response.setContentType(contentType);
		}

		response.getWriter().write(content);
		if (response.isLogEnabled()) {
			response.log(content);
		}
	}

	public String getTextContentType() {
		return null;
	}
}

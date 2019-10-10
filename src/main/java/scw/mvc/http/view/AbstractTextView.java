package scw.mvc.http.view;

import scw.core.utils.StringUtils;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.HttpView;
import scw.mvc.http.Text;
import scw.net.mime.MimeTypeConstants;

public abstract class AbstractTextView extends HttpView implements Text {

	@Override
	public void render(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse) throws Throwable {
		String content = getTextContent();
		String contentType = getTextContentType();
		if (StringUtils.isEmpty(contentType)) {
			if (StringUtils.isEmpty(httpResponse.getContentType())) {
				httpResponse.setContentType(MimeTypeConstants.TEXT_HTML_VALUE);
			}
		} else {
			httpResponse.setContentType(contentType);
		}

		httpResponse.getWriter().write(content);
		if (channel.isLogEnabled()) {
			channel.log(content);
		}
	}

	public String getTextContentType() {
		return null;
	}
}

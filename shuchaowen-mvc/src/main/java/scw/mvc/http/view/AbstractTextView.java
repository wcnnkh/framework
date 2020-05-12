package scw.mvc.http.view;

import scw.mvc.http.HttpChannel;
import scw.mvc.http.ServerHttpRequest;
import scw.mvc.http.ServerHttpResponse;
import scw.mvc.http.HttpView;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.Text;

public abstract class AbstractTextView extends HttpView implements Text {

	@Override
	public void render(HttpChannel channel, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) throws Throwable {
		String content = getTextContent();
		MimeType mimeType = getMimeType();
		if (mimeType != null) {
			serverHttpResponse.setContentType(mimeType);
		} else {
			serverHttpResponse.setContentType(MimeTypeUtils.TEXT_HTML);
		}

		serverHttpResponse.getWriter().write(content);
		if (channel.isLogEnabled()) {
			channel.log(content);
		}
	}

	public MimeType getMimeType() {
		return null;
	}
}

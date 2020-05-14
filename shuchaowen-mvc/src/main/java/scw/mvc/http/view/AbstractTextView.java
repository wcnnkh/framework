package scw.mvc.http.view;

import scw.mvc.Channel;
import scw.mvc.http.HttpView;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.net.message.Text;

public abstract class AbstractTextView extends HttpView implements Text {

	@Override
	public void render(Channel channel, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) throws Throwable {
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

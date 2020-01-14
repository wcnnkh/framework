package scw.mvc.http.view;

import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.HttpView;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.Text;

public abstract class AbstractTextView extends HttpView implements Text {

	@Override
	public void render(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse) throws Throwable {
		String content = getTextContent();
		MimeType mimeType = getMimeType();
		if (mimeType != null) {
			httpResponse.setMimeType(mimeType);
		} else {
			httpResponse.setMimeType(MimeTypeUtils.TEXT_HTML);
		}

		httpResponse.getWriter().write(content);
		if (channel.isLogEnabled()) {
			channel.log(content);
		}
	}

	public MimeType getMimeType() {
		return null;
	}
}

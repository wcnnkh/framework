package scw.mvc.view;

import java.io.IOException;

import scw.mvc.HttpChannel;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.web.model.Text;

public abstract class AbstractTextView implements View, Text {

	public void render(HttpChannel httpChannel) throws IOException {
		String content = toTextContent();
		MimeType mimeType = getMimeType();
		if (mimeType != null) {
			httpChannel.getResponse().setContentType(mimeType);
		} else {
			httpChannel.getResponse().setContentType(MimeTypeUtils.TEXT_HTML);
		}

		httpChannel.getResponse().getWriter().write(content);
	}

	public MimeType getMimeType() {
		return null;
	}
	
	@Override
	public String toString() {
		return toTextContent();
	}
}

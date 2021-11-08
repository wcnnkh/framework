package io.basc.framework.mvc.view;

import io.basc.framework.mvc.HttpChannel;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.web.message.model.Text;

import java.io.IOException;

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

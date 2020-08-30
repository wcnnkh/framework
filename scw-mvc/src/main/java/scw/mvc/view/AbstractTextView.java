package scw.mvc.view;

import java.io.IOException;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.HttpChannel;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.Text;

public abstract class AbstractTextView implements View, Text {
	private static Logger logger = LoggerUtils.getLogger(AbstractTextView.class);

	public void render(HttpChannel httpChannel) throws IOException {
		String content = getTextContent();
		MimeType mimeType = getMimeType();
		if (mimeType != null) {
			httpChannel.getResponse().setContentType(mimeType);
		} else {
			httpChannel.getResponse().setContentType(MimeTypeUtils.TEXT_HTML);
		}

		httpChannel.getResponse().getWriter().write(content);
		if (logger.isDebugEnabled()) {
			logger.debug(content);
		}
	}

	public MimeType getMimeType() {
		return null;
	}
}

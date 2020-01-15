package scw.net.message.converter;

import java.io.IOException;

import scw.io.IOUtils;
import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public abstract class AbstractTextMessageConverter extends AbstractMessageConverter {
	private String defaultCharsetName;

	public String getDefaultCharsetName() {
		return defaultCharsetName;
	}

	public void setDefaultCharsetName(String defaultCharsetName) {
		this.defaultCharsetName = defaultCharsetName;
	}

	protected String getCharsetName(MimeType mimeType) {
		if (mimeType == null) {
			return getDefaultCharsetName();
		}

		String charsetName = mimeType.getCharsetName();
		if (charsetName == null) {
			return getDefaultCharsetName();
		}

		return charsetName;
	}

	protected String read(InputMessage inputMessage) throws IOException {
		return IOUtils.readContent(inputMessage.getBody(), getCharsetName(inputMessage.getContentType()));
	}

	protected void write(String text, MimeType contentType, OutputMessage outputMessage) throws IOException {
		IOUtils.write(text, outputMessage.getBody(), getCharsetName(contentType));
	}
}

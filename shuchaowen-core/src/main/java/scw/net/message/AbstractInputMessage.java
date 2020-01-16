package scw.net.message;

import java.io.IOException;

import scw.core.Constants;
import scw.core.string.StringCodecUtils;
import scw.io.IOUtils;
import scw.net.MimeType;
import scw.net.message.converter.MessageConvertException;

public abstract class AbstractInputMessage extends AbstractMessage implements InputMessage {

	public byte[] toByteArray() throws IOException {
		return IOUtils.toByteArray(getBody());
	}

	public String convertToString(String charsetName) throws IOException, MessageConvertException {
		byte[] data = toByteArray();
		if (data == null) {
			return null;
		}

		return StringCodecUtils.getStringCodec(charsetName).decode(data);
	}

	protected String getDefaultCharsetName() {
		return Constants.DEFAULT_CHARSET_NAME;
	}

	public String convertToString() throws IOException, MessageConvertException {
		return convertToString(getCharsetName());
	}

	public String getCharsetName() {
		String charsetName = null;
		MimeType mimeType = getContentType();
		if (mimeType != null) {
			charsetName = mimeType.getCharsetName();
		}

		if (charsetName == null) {
			charsetName = getDefaultCharsetName();
		}

		if (charsetName == null) {
			charsetName = Constants.DEFAULT_CHARSET_NAME;
		}
		return charsetName;
	}
}

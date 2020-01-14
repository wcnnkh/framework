package scw.net.message;

import scw.core.Constants;
import scw.core.string.StringCodecUtils;
import scw.net.mime.MimeType;

public abstract class AbstractInputMessage implements InputMessage {

	public String toString(String charsetName) {
		byte[] data = toByteArray();
		if (data == null) {
			return null;
		}

		return StringCodecUtils.getStringCodec(charsetName).decode(data);
	}

	protected abstract String getDefaultCharsetName();

	@Override
	public String toString() {
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
		return toString(charsetName);
	}
}

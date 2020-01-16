package scw.net.message;

import java.io.Serializable;

import scw.core.string.StringCodecUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.net.message.converter.MessageConvertException;

public class SerialzerableInputMessage extends AbstractInputMessage implements
		Serializable {
	private static final long serialVersionUID = 1L;
	private final byte[] body;
	private final Headers headers;

	public SerialzerableInputMessage(byte[] body, Headers headers) {
		this.body = body;
		this.headers = headers;
		headers.readyOnly();
	}

	public UnsafeByteArrayInputStream getBody() {
		return body == null ? null : new UnsafeByteArrayInputStream(body);
	}

	public Headers getHeaders() {
		return headers;
	}

	public byte[] toByteArray() {
		return body == null ? null : body.clone();
	}

	public String convertToString() {
		return convertToString(getCharsetName());
	}

	@Override
	public String convertToString(String charsetName)
			throws MessageConvertException {
		byte[] data = toByteArray();
		if (data == null) {
			return null;
		}

		return StringCodecUtils.getStringCodec(charsetName).decode(data);
	}

	@Override
	public String toString() {
		return convertToString();
	}
}

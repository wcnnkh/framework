package scw.net.message;

import java.io.Serializable;

import scw.compatible.CompatibleUtils;
import scw.io.UnsafeByteArrayInputStream;

public class SerializableInputMessage extends AbstractInputMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private final byte[] body;
	private final Headers headers;

	public SerializableInputMessage(byte[] body, Headers headers) {
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

	@Override
	public String getTextBody() {
		if (body == null) {
			return null;
		}

		return CompatibleUtils.getStringOperations().createString(body, getDefaultCharset());
	}
}

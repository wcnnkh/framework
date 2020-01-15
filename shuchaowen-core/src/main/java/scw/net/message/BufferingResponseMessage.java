package scw.net.message;

import java.io.IOException;
import java.io.InputStream;

import scw.io.UnsafeByteArrayInputStream;

public class BufferingResponseMessage extends AbstractInputMessage {
	private final byte[] body;
	private final Headers headers;

	public BufferingResponseMessage(byte[] body, Headers headers) {
		this.body = body;
		this.headers = headers;
		headers.readyOnly();
	}

	public InputStream getBody() throws IOException {
		return body == null ? null : new UnsafeByteArrayInputStream(body);
	}

	public Headers getHeaders() {
		return headers;
	}

	@Override
	public byte[] toByteArray() throws MessageConvetException {
		try {
			return super.toByteArray();
		} catch (IOException e) {
			throw new MessageConvetException(e);
		}
	}

	@Override
	public String convertToString() throws MessageConvetException {
		try {
			return super.convertToString();
		} catch (IOException e) {
			throw new MessageConvetException(e);
		}
	}

	@Override
	public String convertToString(String charsetName) throws MessageConvetException {
		try {
			return super.convertToString(charsetName);
		} catch (IOException e) {
			throw new MessageConvetException(e);
		}
	}

	@Override
	public String toString() {
		return convertToString();
	}
}

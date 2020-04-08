package scw.net.http;

import scw.net.message.SerializableInputMessage;

public class SerializableHttpInputMessage extends SerializableInputMessage {
	private static final long serialVersionUID = 1L;

	public SerializableHttpInputMessage(byte[] body, HttpHeaders headers) {
		super(body, headers);
	}

	@Override
	public HttpHeaders getHeaders() {
		return (HttpHeaders) super.getHeaders();
	}
}

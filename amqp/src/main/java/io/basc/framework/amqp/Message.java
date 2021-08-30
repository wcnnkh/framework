package io.basc.framework.amqp;

public class Message extends MessageProperties {
	private static final long serialVersionUID = 1L;
	private final byte[] body;

	public Message(byte[] body) {
		this.body = body;
	}

	public final byte[] getBody() {
		return body;
	}
}

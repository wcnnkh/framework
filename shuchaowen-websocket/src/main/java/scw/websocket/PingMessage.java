package scw.websocket;

import java.nio.ByteBuffer;

import scw.util.message.BinaryMessage;

public class PingMessage extends BinaryMessage {
	private static final long serialVersionUID = 1L;

	public PingMessage(ByteBuffer payload) {
		super(payload);
	}

	public PingMessage(byte[] payload) {
		this(payload, 0, payload.length);
	}

	public PingMessage(byte[] payload, int offset, int length) {
		super(ByteBuffer.wrap(payload, offset, length));
	}
}

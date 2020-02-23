package scw.websocket;

import java.nio.ByteBuffer;

import scw.message.BinaryMessage;

public class PongMessage extends BinaryMessage {
	private static final long serialVersionUID = 1L;

	public PongMessage(ByteBuffer payload) {
		super(payload);
	}

	public PongMessage(byte[] payload) {
		this(payload, 0, payload.length);
	}

	public PongMessage(byte[] payload, int offset, int length) {
		super(ByteBuffer.wrap(payload, offset, length));
	}
}

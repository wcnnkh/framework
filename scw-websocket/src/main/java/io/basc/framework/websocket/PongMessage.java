package io.basc.framework.websocket;

import io.basc.framework.messageing.BinaryMessage;

import java.nio.ByteBuffer;

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

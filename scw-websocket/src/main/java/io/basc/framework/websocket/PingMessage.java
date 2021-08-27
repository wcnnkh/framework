package io.basc.framework.websocket;

import io.basc.framework.messageing.BinaryMessage;

import java.nio.ByteBuffer;

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

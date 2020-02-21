package scw.message;

import java.nio.ByteBuffer;

public class BinaryMessage extends AbstractMessage<ByteBuffer> {

	public BinaryMessage(ByteBuffer payload) {
		super(payload);
	}

	public BinaryMessage(byte[] payload) {
		this(payload, 0, payload.length);
	}

	public BinaryMessage(byte[] payload, int offset, int length) {
		super(ByteBuffer.wrap(payload, offset, length));
	}

	public int getPayloadLength() {
		return getPayload().remaining();
	}

	@Override
	protected String toStringPayload() {
		return getPayload().toString();
	}

}
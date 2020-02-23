package scw.util.message;

import java.nio.ByteBuffer;

public class BinaryFragmentMessage extends BinaryMessage implements FragmentMessage<ByteBuffer>{
	private static final long serialVersionUID = 1L;
	private boolean last;
	
	public BinaryFragmentMessage(ByteBuffer payload, boolean last) {
		super(payload);
		this.last = last;
	}

	public BinaryFragmentMessage(byte[] payload, boolean last) {
		super(payload, 0, payload.length);
		this.last = last;
	}

	public BinaryFragmentMessage(byte[] payload, int offset, int length, boolean last) {
		super(ByteBuffer.wrap(payload, offset, length));
		this.last = last;
	}

	public boolean isLast() {
		return last;
	}
}

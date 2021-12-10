package io.basc.framework.messageing;

public class ObjectFragmentMessage<T> extends AbstractMessage<T> implements FragmentMessage<T> {
	private int length;
	private boolean last;

	public ObjectFragmentMessage(T payload, int length, boolean last) {
		super(payload);
		this.length = length;
		this.last = last;
	}

	public int getPayloadLength() {
		return length;
	}

	@Override
	protected String toStringPayload() {
		return getPayload().toString();
	}

	public boolean isLast() {
		return last;
	}

}

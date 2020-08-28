package scw.messageing;

public class ObjectMessage<T> extends AbstractMessage<T> {
	private int length;

	public ObjectMessage(T payload, int length) {
		super(payload);
		this.length = length;
	}

	public int getPayloadLength() {
		return length;
	}

	@Override
	protected String toStringPayload() {
		return getPayload().toString();
	}

}

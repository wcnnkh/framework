package scw.message;

public class TextMessage extends AbstractMessage<String>{

	TextMessage(String payload) {
		super(payload);
	}

	public int getPayloadLength() {
		return getPayload().length();
	}

	@Override
	protected String toStringPayload() {
		return getPayload();
	}
}

package io.basc.framework.messageing;

import java.io.Serializable;


public class TextMessage extends AbstractMessage<String> implements Serializable{
	private static final long serialVersionUID = 1L;

	public TextMessage(String payload) {
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

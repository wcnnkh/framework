package io.basc.framework.jms.messageing;

import io.basc.framework.messageing.Message;
import io.basc.framework.messageing.MessageHeaders;
import lombok.Data;

@Data
public class JmsMessage implements Message<Object> {
	private final javax.jms.Message message;

	@Override
	public Object getPayload() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageHeaders getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}

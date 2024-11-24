package io.basc.framework.jms.messageing;

import javax.jms.Message;

import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.messageing.MessageHeaders;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class JmsMessageHeaders extends MessageHeaders {
	private static final long serialVersionUID = 1L;
	private final Message message;

	public JmsMessageHeaders(Message message) {
		this.message = message;
	}
	
	@Override
	public ValueWrapper get(Object key) {
		// TODO Auto-generated method stub
		return super.get(key);
	}
}

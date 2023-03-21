package io.basc.framework.amqp;

import io.basc.framework.util.Assert;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Message<T> extends MessageProperties {
	private static final long serialVersionUID = 1L;
	private final T body;

	public Message(T body) {
		Assert.requiredArgument(body != null, "body");
		this.body = body;
	}

	public Message(MessageProperties messageProperties, T body) {
		super(messageProperties);
		Assert.requiredArgument(body != null, "body");
		this.body = body;
	}

	public final T getBody() {
		return body;
	}

	@Override
	public Message<T> clone() {
		return new Message<T>(this, body);
	}
}

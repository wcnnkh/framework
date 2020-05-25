package scw.amqp;

import java.io.Serializable;

public class MethodMessage extends MessageProperties implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Object[] args;

	public MethodMessage(Object... args) {
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}
}

package io.basc.framework.amqp;

public class MethodMessage extends MessageProperties {
	private static final long serialVersionUID = 1L;
	private final Object[] args;

	public MethodMessage(Object... args) {
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}
}

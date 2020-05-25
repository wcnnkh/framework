package scw.amqp;

import scw.core.GlobalPropertyFactory;

public class ExchangeDeclare extends Declare {
	public static final String DEFAULT_TYPE = GlobalPropertyFactory.getInstance().getValue("amqp.exchange.type",
			String.class, "direct");
	private static final long serialVersionUID = 1L;
	private String type = DEFAULT_TYPE;
	private boolean internal;

	public ExchangeDeclare(String name) {
		super(name);
	}
	
	@Override
	public ExchangeDeclare setName(String name) {
		super.setName(name);
		return this;
	}

	public String getType() {
		return type;
	}

	public ExchangeDeclare setType(String type) {
		this.type = type;
		return this;
	}

	public boolean isInternal() {
		return internal;
	}

	public ExchangeDeclare setInternal(boolean internal) {
		this.internal = internal;
		return this;
	}

	public String toString() {
		return "Exchange [name=" + getName() + ", type=" + getType() + ", durable=" + isDurable() + ", autoDelete="
				+ isAutoDelete() + ", internal=" + isInternal() + ", arguments=" + getArguments() + "]";
	};
}

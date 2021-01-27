package scw.amqp;

public class ExchangeDeclare extends Declare {
	private static final long serialVersionUID = 1L;
	private String type;
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

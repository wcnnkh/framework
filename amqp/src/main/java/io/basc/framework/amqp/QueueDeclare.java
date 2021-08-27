package io.basc.framework.amqp;

public class QueueDeclare extends Declare {
	private static final long serialVersionUID = 1L;
	private boolean exclusive;

	public QueueDeclare(String name) {
		super(name);
	}

	@Override
	public QueueDeclare setName(String name) {
		super.setName(name);
		return this;
	}

	public boolean isExclusive() {
		return exclusive;
	}

	public QueueDeclare setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
		return this;
	}

	@Override
	public String toString() {
		return "Queue [name=" + getName() + ", durable=" + isDurable() + ", autoDelete=" + isAutoDelete()
				+ ", exclusive=" + isExclusive() + ", arguments=" + getArguments() + "]";
	}
}

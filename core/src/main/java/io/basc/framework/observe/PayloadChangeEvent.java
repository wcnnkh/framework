package io.basc.framework.observe;

public class PayloadChangeEvent<T> extends ChangeEvent {
	private static final long serialVersionUID = 1L;
	private final T payload;

	public PayloadChangeEvent(Object source, ChangeType type, T payload) {
		super(source, type);
		this.payload = payload;
	}

	public T getPayload() {
		return payload;
	}
}

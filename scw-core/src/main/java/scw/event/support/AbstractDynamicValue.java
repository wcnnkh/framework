package scw.event.support;

import java.util.concurrent.atomic.AtomicBoolean;

import scw.event.EventListener;
import scw.event.EventRegistration;

public abstract class AbstractDynamicValue<T> implements DynamicValue<T> {
	private T value;
	private EventRegistration eventRegistration;
	private volatile AtomicBoolean state = new AtomicBoolean(false);

	public final boolean isDynamic() {
		return state.get();
	}

	public final boolean switchDynamicState(boolean dynamic) {
		if (state.compareAndSet(state.get(), dynamic)) {
			if (dynamic) {
				if (eventRegistration == null) {
					eventRegistration = registerDynamicListener();
				}
			} else {
				if (eventRegistration != null) {
					eventRegistration.unregister();
				}
			}
		}
		return false;
	}

	protected EventRegistration registerDynamicListener() {
		return registerListener(new EventListener<ValueEvent<T>>() {

			public void onEvent(ValueEvent<T> event) {
				setValue(event.getValue());
			}
		});
	}

	@Override
	protected void finalize() throws Throwable {
		switchDynamicState(false);
		super.finalize();
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}

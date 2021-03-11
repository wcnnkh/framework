package scw.event;

import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.Ordered;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.util.CacheableSupplier;
import scw.util.Supplier;

public abstract class AbstractObservable<T> implements Observable<T>,
		EventListener<ChangeEvent<T>>, Ordered{
	private BasicEventDispatcher<ChangeEvent<T>> dispatcher = new DefaultBasicEventDispatcher<ChangeEvent<T>>(
			true);
	private CacheableSupplier<T> valueSupplier = new CacheableSupplier<T>(new Supplier<T>() {
		public T get() {
			return forceGet();
		};
	});
	private volatile EventRegistration eventRegistration;
	private volatile AtomicBoolean registered = new AtomicBoolean(false);
	private boolean registerOnlyExists = true;
	private int order;
	
	public int getOrder() {
		return order;
	}

	/**
	 * 当存在多个Observable时，获取数据的优先级
	 * @see Observables#forceGet()
	 * @param order
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	public boolean unregister() {
		if (eventRegistration != null && registered.compareAndSet(true, false)) {
			eventRegistration.unregister();
			eventRegistration = null;
			return true;
		}
		return false;
	}

	public T get() {
		return valueSupplier.get();
	}

	protected void set(T value) {
		this.valueSupplier.setCache(value);
	}

	public boolean isRegistered() {
		return registered.get();
	}

	public boolean isRegisterOnlyExists() {
		return registerOnlyExists;
	}

	public void setRegisterOnlyExists(boolean registerOnlyExists) {
		this.registerOnlyExists = registerOnlyExists;
	}

	/**
	 * @see #isRegisterOnlyExists()
	 * @see #register(boolean)
	 */
	public final boolean register() {
		return register(isRegisterOnlyExists());
	}

	public boolean register(boolean exists) {
		if (isRegistered()) {
			return false;
		}

		if (registered.compareAndSet(false, true)) {
			eventRegistration = registerListener(exists, this);
			return true;
		}
		return false;
	}

	public BasicEventRegistry<ChangeEvent<T>> getRegistry() {
		return dispatcher;
	}

	public final EventRegistration registerListener(
			EventListener<ChangeEvent<T>> eventListener) {
		return registerListener(isRegisterOnlyExists(), eventListener);
	}

	public void onEvent(ChangeEvent<T> event) {
		set(event.getSource());
		dispatcher.publishEvent(event);
	}
}

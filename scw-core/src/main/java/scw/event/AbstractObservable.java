package scw.event;

import scw.event.support.DefaultEventDispatcher;
import scw.util.CacheableSupplier;
import scw.util.Supplier;

public abstract class AbstractObservable<T> extends
		DefaultEventDispatcher<ChangeEvent<T>> implements Observable<T> {
	private final CacheableSupplier<T> valueSupplier = new CacheableSupplier<T>(
			new Supplier<T>() {
				public T get() {
					return forceGet();
				};
			});

	public AbstractObservable() {
		super(true);
	}

	protected abstract T forceGet();

	public T get() {
		return valueSupplier.get();
	}

	@Override
	public void publishEvent(ChangeEvent<T> event) {
		valueSupplier.setCache(event.getSource());
		super.publishEvent(event);
	}
	
	@Override
	public String toString() {
		return String.valueOf(get());
	}
}

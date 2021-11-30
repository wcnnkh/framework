package io.basc.framework.event;

import io.basc.framework.event.support.SimpleEventDispatcher;
import io.basc.framework.util.CacheableSupplier;

public abstract class AbstractObservable<T> extends
		SimpleEventDispatcher<ChangeEvent<T>> implements Observable<T> {
	private final CacheableSupplier<T> valueSupplier = new CacheableSupplier<T>(() -> forceGet());

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
		//即便publish出现异常值也在上一步插入了
		super.publishEvent(event);
	}
	
	@Override
	public String toString() {
		return String.valueOf(get());
	}
}

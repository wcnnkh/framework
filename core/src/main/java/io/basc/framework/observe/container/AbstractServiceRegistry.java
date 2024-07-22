package io.basc.framework.observe.container;

import java.util.function.Supplier;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.register.RegistryEvent;
import lombok.NonNull;

public abstract class AbstractServiceRegistry<E, C> extends ObservableContainer<RegistryEvent<E>, C> implements ServiceRegistry<E> {

	public AbstractServiceRegistry(@NonNull Supplier<? extends C> containerSupplier) {
		super(containerSupplier);
	}

	/**
	 * 默认直接推送全部更新事件
	 */
	@Override
	public void reload() {
		publishBatchEvent(getServices().map((e) -> new RegistryEvent<>(this, ChangeType.UPDATE, e)));
	}

	@Override
	public String toString() {
		return getServices().toString();
	}
}

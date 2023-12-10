package io.basc.framework.observe.register;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.Observer;
import io.basc.framework.util.RegistrationException;
import io.basc.framework.util.Registrations;

public abstract class AbstractRegistry<E> extends Observer<RegistryEvent<E>> implements Registry<E> {

	@Override
	public final Registrations<ElementRegistration<E>> registers(Iterable<? extends E> elements)
			throws RegistrationException {
		return Registry.super.registers(elements);
	}

	/**
	 * 默认直接推送全部更新事件
	 */
	@Override
	public void reload() {
		publishBatchEvent(getServices().map((e) -> new RegistryEvent<>(this, ChangeType.UPDATE, e)));
	}
}

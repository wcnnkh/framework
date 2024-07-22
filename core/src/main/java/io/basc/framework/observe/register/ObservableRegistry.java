package io.basc.framework.observe.register;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.Observable;
import io.basc.framework.register.PayloadBatchRegistration;
import io.basc.framework.register.PayloadRegistration;
import io.basc.framework.register.Registration;

public class ObservableRegistry<E extends Observable<?>> extends ObservableList<E> {

	@Override
	protected PayloadBatchRegistration<E> batch(PayloadBatchRegistration<E> batchRegistration) {
		return super.batch(batchRegistration).batch((elements) -> Registration
				.registers(elements.map(PayloadRegistration::getPayload), (e) -> e.registerBatchListener((events) -> {
					publishEvent(new RegistryEvent<>(this, ChangeType.UPDATE, e));
				})));
	}
}

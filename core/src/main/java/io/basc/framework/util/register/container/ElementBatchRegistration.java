package io.basc.framework.util.register.container;

import java.util.function.Function;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.ServiceBatchRegistration;
import lombok.NonNull;

public class ElementBatchRegistration<T> extends ServiceBatchRegistration<T, ElementRegistration<T>> {
	protected ElementBatchRegistration(BatchRegistration<ElementRegistration<T>> batchRegistration) {
		super(batchRegistration);
	}

	public ElementBatchRegistration(@NonNull Elements<ElementRegistration<T>> registrations) {
		super(registrations, (a, b) -> a.and(b));
	}

	@Override
	public ElementBatchRegistration<T> batch(
			@NonNull Function<? super Elements<ElementRegistration<T>>, ? extends Registration> batchMapper) {
		return new ElementBatchRegistration<>(super.batch(batchMapper));
	}

}

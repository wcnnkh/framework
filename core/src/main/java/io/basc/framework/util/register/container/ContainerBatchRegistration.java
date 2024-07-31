package io.basc.framework.util.register.container;

import java.util.function.BiFunction;

import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.Registration;
import lombok.NonNull;

public class ContainerBatchRegistration<T, R extends ElementRegistration<T>> extends BatchRegistration<R> {

	public ContainerBatchRegistration(@NonNull Elements<R> registrations,
			@NonNull BiFunction<? super R, ? super Registration, ? extends R> andFunction) {
		super(new DisposableLimiter(), registrations, andFunction);
	}
}

package io.basc.framework.util.register;

import java.util.function.BiFunction;
import java.util.function.Function;

import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.element.Elements;
import lombok.NonNull;

public class ServiceBatchRegistration<S, R extends ServiceRegistration<S>> extends BatchRegistration<R> {

	public ServiceBatchRegistration(@NonNull Elements<R> registrations,
			@NonNull BiFunction<? super R, ? super Registration, ? extends R> andFunction) {
		super(new DisposableLimiter(), registrations, andFunction);
	}

	protected ServiceBatchRegistration(BatchRegistration<R> batchRegistration) {
		super(batchRegistration);
	}

	@Override
	public ServiceBatchRegistration<S, R> batch(
			@NonNull Function<? super Elements<R>, ? extends Registration> batchMapper) {
		return new ServiceBatchRegistration<>(super.batch(batchMapper));
	}

	@Override
	public ServiceBatchRegistration<S, R> and(@NonNull R registration) {
		return new ServiceBatchRegistration<>(super.and(registration));
	}

	@Override
	public ServiceBatchRegistration<S, R> andAll(@NonNull Elements<? extends R> registrations) {
		return new ServiceBatchRegistration<>(super.andAll(registrations));
	}
}

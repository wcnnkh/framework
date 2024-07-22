package io.basc.framework.register;

import java.util.function.BiFunction;
import java.util.function.Function;

import io.basc.framework.limit.DisposableLimiter;
import io.basc.framework.util.element.Elements;
import lombok.NonNull;

public class BatchRegistration<T extends AbstractRegistration> extends Registrations<T> {
	private final BiFunction<? super T, ? super Registration, ? extends T> andFunction;

	public BatchRegistration(Elements<T> elements,
			@NonNull BiFunction<? super T, ? super Registration, ? extends T> andFunction) {
		super(new DisposableLimiter(), elements);
		this.andFunction = andFunction;
	}

	protected BatchRegistration(BatchRegistration<T> batchRegistration) {
		this(batchRegistration, batchRegistration.andFunction);
	}

	private BatchRegistration(Registrations<T> registrations,
			BiFunction<? super T, ? super Registration, ? extends T> andFunction) {
		super(registrations);
		this.andFunction = andFunction;
	}

	public BatchRegistration<T> batch(Function<? super Elements<T>, ? extends Registration> batchMapper) {
		Registrations<T> registrations = and(() -> {
			Elements<T> source = getServices().filter((e) -> e.getLimiter().limited());
			Registration registration = batchMapper.apply(source);
			registration.unregister();
		});
		registrations = registrations.map((e) -> {
			Registration registration = batchMapper.apply(Elements.singleton(e));
			return andFunction.apply(e, registration);
		});
		return new BatchRegistration<>(registrations, this.andFunction);
	}
}

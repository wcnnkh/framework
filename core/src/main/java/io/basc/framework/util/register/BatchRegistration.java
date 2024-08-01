package io.basc.framework.util.register;

import java.util.function.BiFunction;
import java.util.function.Function;

import io.basc.framework.util.concurrent.limit.Limiter;
import io.basc.framework.util.element.Elements;
import lombok.NonNull;

/**
 * 批量注册
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public class BatchRegistration<E extends CombinableRegistration<Registration>>
		extends InterceptableRegisration<Registration, E, Registration> {
	@NonNull
	private final BiFunction<? super E, ? super Registration, ? extends E> andFunction;

	private BatchRegistration(@NonNull InterceptableRegisration<Registration, E, Registration> context,
			@NonNull BiFunction<? super E, ? super Registration, ? extends E> andFunction) {
		super(context);
		this.andFunction = andFunction;
	}
	
	public BatchRegistration(@NonNull Limiter limiter, @NonNull Elements<E> registrations,
			@NonNull BiFunction<? super E, ? super Registration, ? extends E> andFunction) {
		super(limiter, registrations);
		this.andFunction = andFunction;
	}

	protected BatchRegistration(BatchRegistration<E> batchRegistration) {
		this(batchRegistration, batchRegistration.andFunction);
	}

	@Override
	public BatchRegistration<E> and(@NonNull E registration) {
		return new BatchRegistration<>(super.and(registration), this.andFunction);
	}

	@Override
	public BatchRegistration<E> andAll(@NonNull Elements<? extends E> registrations) {
		return new BatchRegistration<>(super.andAll(registrations), this.andFunction);
	}

	public BatchRegistration<E> batch(@NonNull Function<? super Elements<E>, ? extends Registration> batchMapper) {
		return new BatchRegistration<>(super.pre(() -> {
			// 限制各元素的行为
			Elements<E> source = getRegistrations().filter((e) -> e.getLimiter().limited());
			Registration registration = batchMapper.apply(source);
			try {
				// 执行批量行为
				registration.deregister();
			} finally {
				// 执行原始行为，一般是空的实现
				source.forEach((e) -> e.getRegistrations().forEach(Registration::deregister));
			}
		}).map((r) -> {
			// 为每个元素添加行为
			Registration registration = batchMapper.apply(Elements.singleton(r));
			return andFunction.apply(r, registration);
		}), this.andFunction);
	}
}

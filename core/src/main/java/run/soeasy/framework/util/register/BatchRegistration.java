package run.soeasy.framework.util.register;

import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.exchange.Registration;

/**
 * 批量注册
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public class BatchRegistration<E extends LimitableRegistration>
		extends InterceptableRegisration<Registration, E, Registration> {
	@NonNull
	private final BiFunction<? super E, ? super Registration, ? extends E> andFunction;

	private BatchRegistration(@NonNull InterceptableRegisration<Registration, E, Registration> context,
			@NonNull BiFunction<? super E, ? super Registration, ? extends E> andFunction) {
		super(context);
		this.andFunction = andFunction;
	}

	public BatchRegistration(@NonNull Elements<E> registrations,
			@NonNull BiFunction<? super E, ? super Registration, ? extends E> andFunction) {
		super(registrations);
		this.andFunction = andFunction;
	}

	protected BatchRegistration(BatchRegistration<E> batchRegistration) {
		this(batchRegistration, batchRegistration.andFunction);
	}

	@Override
	public BatchRegistration<E> combine(@NonNull E registration) {
		return new BatchRegistration<>(super.combine(registration), this.andFunction);
	}

	@Override
	public BatchRegistration<E> combineAll(@NonNull Elements<? extends E> registrations) {
		return new BatchRegistration<>(super.combineAll(registrations), this.andFunction);
	}

	public BatchRegistration<E> batch(@NonNull Function<? super Elements<E>, ? extends Registration> batchMapper) {
		Registration batch = new DisposableRegistration(() -> {
			// 限制各元素的行为
			Elements<E> source = getElements().filter((e) -> e.getLimiter().limited());
			Registration registration = batchMapper.apply(source);
			// 执行批量行为
			registration.cancel();
		});

		return new BatchRegistration<>(super.pre(batch).map((r) -> {
			// 为每个元素添加行为
			Registration registration = batchMapper.apply(Elements.singleton(r));
			return andFunction.apply(r, registration);
		}), this.andFunction);
	}
}

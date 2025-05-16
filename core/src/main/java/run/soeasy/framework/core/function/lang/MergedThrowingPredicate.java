package run.soeasy.framework.core.function.lang;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.ThrowingFunction;

@RequiredArgsConstructor
@Getter
public class MergedThrowingPredicate<S, E extends Throwable, T, R extends Throwable>
		implements ThrowingPredicate<T, R> {
	@NonNull
	private final ThrowingFunction<? super T, ? extends S, ? extends R> mapper;
	@NonNull
	private final ThrowingPredicate<? super S, ? extends E> predicate;
	@NonNull
	private final Function<? super E, ? extends R> throwingMapper;
	@NonNull
	private final ThrowingConsumer<? super S, ? extends R> endpoint;

	@SuppressWarnings("unchecked")
	@Override
	public boolean test(T target) throws R {
		S source = mapper.apply(target);
		try {
			return predicate.test(source);
		} catch (Throwable e) {
			throw throwingMapper.apply((E) e);
		} finally {
			endpoint.accept(source);
		}
	}
}
package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class MappingThrowingFunction<S, T, E extends Throwable, V, R extends Throwable> implements ThrowingFunction<S, V, R> {
	@NonNull
	private final ThrowingFunction<? super S, ? extends T, ? extends E> compose;
	@NonNull
	private final ThrowingFunction<? super T, ? extends V, ? extends R> andThen;
	@NonNull
	private final Function<? super E, ? extends R> throwingMapper;
	@NonNull
	private final ThrowingConsumer<? super T, ? extends E> endpoint;

	@SuppressWarnings("unchecked")
	@Override
	public V apply(S source) throws R {
		T value;
		try {
			value = compose.apply(source);
		} catch (Throwable e) {
			throw throwingMapper.apply((E) e);
		}

		try {
			return andThen.apply(value);
		} catch (Throwable e) {
			throw throwingMapper.apply((E) e);
		} finally {
			// endpoint.accept(value);
		}
	}
}
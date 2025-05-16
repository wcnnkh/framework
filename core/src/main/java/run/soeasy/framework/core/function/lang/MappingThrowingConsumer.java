package run.soeasy.framework.core.function.lang;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.ThrowingFunction;

@RequiredArgsConstructor
@Getter
public class MappingThrowingConsumer<S, E extends Throwable, T, R extends Throwable> implements ThrowingConsumer<T, R> {
	@NonNull
	private final ThrowingFunction<? super T, ? extends S, ? extends R> mapper;
	@NonNull
	private final ThrowingConsumer<? super S, ? extends E> compose;
	@NonNull
	private final ThrowingConsumer<? super S, ? extends R> andThen;
	@NonNull
	private final Function<? super E, ? extends R> throwingMapper;
	@NonNull
	private final ThrowingConsumer<? super S, ? extends R> endpoint;

	@SuppressWarnings("unchecked")
	@Override
	public void accept(T target) throws R {
		S source = mapper.apply(target);
		try {
			compose.accept(source);
			andThen.accept(source);
		} catch (Throwable e) {
			throw throwingMapper.apply((E) e);
		} finally {
			endpoint.accept(source);
		}
	}
}

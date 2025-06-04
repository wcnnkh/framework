package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
class MappingThrowingRunnable<E extends Throwable, R extends Throwable> implements ThrowingRunnable<R> {
	@NonNull
	private final ThrowingRunnable<? extends E> compose;
	@NonNull
	private final ThrowingRunnable<? extends R> andThen;
	@NonNull
	private final Function<? super E, ? extends R> throwingMapper;
	@NonNull
	private final ThrowingRunnable<? extends R> endpoint;

	@SuppressWarnings("unchecked")
	@Override
	public void run() throws R {
		try {
			compose.run();
			andThen.run();
		} catch (Throwable e) {
			throw throwingMapper.apply((E) e);
		} finally {
			endpoint.run();
		}
	}
}
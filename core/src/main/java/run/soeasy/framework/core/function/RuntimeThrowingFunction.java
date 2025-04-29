package run.soeasy.framework.core.function;

import java.util.function.Function;

public interface RuntimeThrowingFunction<S, T, E extends RuntimeException>
		extends ThrowingFunction<S, T, E>, Function<S, T> {
	public static interface RuntimeThrowingFunctionWrapper<S, T, E extends RuntimeException, W extends RuntimeThrowingFunction<S, T, E>>
			extends RuntimeThrowingFunction<S, T, E>, ThrowingFunctionWrapper<S, T, E, W> {
	}
}

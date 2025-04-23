package run.soeasy.framework.core.function;

import java.util.function.Function;

public interface RuntimeThrowingFunction<S, T, E extends RuntimeException>
		extends ThrowingFunction<S, T, E>, Function<S, T> {
}

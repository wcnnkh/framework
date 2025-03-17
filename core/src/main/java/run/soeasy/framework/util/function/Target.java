package run.soeasy.framework.util.function;

import lombok.NonNull;

@FunctionalInterface
public interface Target<T, E extends Throwable> {
	<R> Pipeline<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline);
}

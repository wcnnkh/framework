package io.basc.framework.util;

import lombok.NonNull;

public interface Target<T, E extends Throwable> {
	<R> Pipeline<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline);
}

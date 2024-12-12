package io.basc.framework.util;

import lombok.NonNull;

public interface Target<T, E extends Throwable> {
	<R> Channel<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline);
}

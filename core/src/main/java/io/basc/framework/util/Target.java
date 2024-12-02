package io.basc.framework.util;

import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Target<T, E extends Throwable> {
	@RequiredArgsConstructor
	public static class ChannelOptional<T, E extends Throwable, W extends Channel<T, E>> implements Optional<T, E> {
		private final W source;
		private volatile Supplier<T> supplier;

		@Override
		public T orElse(T other) throws E {
			if (supplier == null) {
				synchronized (this) {
					if (supplier == null) {
						T target = source.get();
						try {
							supplier = () -> target;
						} finally {
							source.close();
						}
					}
				}
			}

			T target = supplier.get();
			return target == null ? other : target;
		}
	}

	default Optional<T, E> export() {
		Channel<T, E> channel = map(Pipeline.identity());
		return new ChannelOptional<>(channel);
	}

	<R> Channel<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline);
}

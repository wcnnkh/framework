package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

class OnClosePool<T, E extends Throwable> extends ChainPool<T, E, E, Pool<T, E>> {

	public OnClosePool(@NonNull Pool<T, E> source, @NonNull Function<? super E, ? extends E> throwingMapper,
			ThrowingConsumer<? super T, ? extends E> closer) {
		super(source, throwingMapper, closer);
	}

	@Override
	public void close(T source) throws E {
		try {
			this.source.close(source);
		} finally {
			super.close(source);
		}
	}
}
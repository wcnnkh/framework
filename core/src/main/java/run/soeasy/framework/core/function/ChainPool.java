package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

class ChainPool<V, S extends Throwable, T extends Throwable, W extends ThrowingSupplier<? extends V, ? extends S>>
		extends ChainThrowingSupplier<V, V, S, T, W> implements Pool<V, T> {
	private final ThrowingConsumer<? super V, ? extends S> closer;

	public ChainPool(@NonNull W source, @NonNull Function<? super S, ? extends T> throwingMapper,
			ThrowingConsumer<? super V, ? extends S> closer) {
		super(source, ThrowingFunction.identity(), ThrowingConsumer.ignore(), throwingMapper, false);
		this.closer = closer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void close(V source) throws T {
		try {
			closer.accept(source);
		} catch (Throwable e) {
			throw throwingMapper.apply((S) e);
		}
	}

	@Override
	public <R> Pipeline<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return Pool.super.map(mapper);
	}
}
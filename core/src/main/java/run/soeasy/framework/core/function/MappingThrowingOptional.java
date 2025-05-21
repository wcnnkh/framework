package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

class MappingThrowingOptional<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<S, E>>
		extends MappingThrowingSupplier<S, V, E, T, W> implements ThrowingOptional<V, T> {

	public MappingThrowingOptional(@NonNull W source, @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
			ThrowingConsumer<? super S, ? extends E> endpoint, @NonNull Function<? super E, ? extends T> throwingMapper,
			boolean singleton, @NonNull ThrowingRunnable<? extends T> closeable) {
		super(source, mapper, endpoint, throwingMapper, singleton, closeable);
	}

	@Override
	public <R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super V, ? extends R, ? extends X> mapper)
			throws T, X {
		V target = super.get();
		return mapper.apply(target);
	}

	@Override
	public <R> MappingThrowingOptional<S, R, E, T, W> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new MappingThrowingOptional<>(this.source, this.mapper.andThen(mapper), endpoint, throwingMapper,
				singleton, closeable);
	}

	@Override
	public ThrowingOptional<V, T> optional() {
		return this;
	}
}
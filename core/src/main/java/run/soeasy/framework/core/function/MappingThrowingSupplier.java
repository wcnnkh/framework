package run.soeasy.framework.core.function;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class MappingThrowingSupplier<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<? extends S, ? extends E>>
		implements Pipeline<V, T> {
	@NonNull
	protected final W source;
	@NonNull
	protected final ThrowingFunction<? super S, ? extends V, T> mapper;
	protected final ThrowingConsumer<? super S, ? extends E> endpoint;
	@NonNull
	protected final Function<? super E, ? extends T> throwingMapper;

	protected final boolean singleton;
	private final AtomicBoolean closed = new AtomicBoolean();
	private volatile Supplier<V> singletonSupplier;
	@NonNull
	protected final ThrowingRunnable<? extends T> closeable;

	@Override
	public Pipeline<V, T> closeable() {
		return this;
	}

	@Override
	public V get() throws T {
		if (singleton) {
			if (singletonSupplier == null) {
				synchronized (this) {
					if (singletonSupplier == null) {
						try {
							V value = run(source);
							singletonSupplier = () -> value;
						} finally {
							closed.set(true);
						}
					}
				}
			}
			return singletonSupplier.get();
		}
		return run(this.source);
	}

	@SuppressWarnings("unchecked")
	public V run(ThrowingSupplier<? extends S, ? extends E> supplier) throws T {
		try {
			S source = supplier.get();
			try {
				return mapper.apply(source);
			} finally {
				endpoint.accept(source);
			}
		} catch (Throwable e) {
			throw throwingMapper.apply((E) e);
		}
	}

	@Override
	public void close() throws T {
		closeable.run();
	}

	@Override
	public boolean isClosed() {
		return singleton && closed.get();
	}

	@Override
	public <R> MappingThrowingSupplier<S, R, E, T, W> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
		return new MappingThrowingSupplier<>(this.source, this.mapper.andThen(mapper), endpoint, throwingMapper,
				singleton, closeable);
	}
}
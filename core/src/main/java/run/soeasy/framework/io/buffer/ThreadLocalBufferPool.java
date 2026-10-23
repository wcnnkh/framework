package run.soeasy.framework.io.buffer;

import java.nio.Buffer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ThreadLocalBufferPool<B extends Buffer> implements BufferPool<B> {
	private final ThreadLocal<BufferPool<B>> threadLocal = new ThreadLocal<>();

	@NonNull
	private final Supplier<? extends BufferPool<B>> bufferPoolSupplier;

	public ThreadLocalBufferPool(int bufferCapacity, IntFunction<? extends B> bufferCreator) {
		this(() -> new QueueBufferPool<>(() -> bufferCreator.apply(bufferCapacity)));
	}

	public ThreadLocalBufferPool(int maxPoolSize, int bufferCapacity, IntFunction<? extends B> bufferCreator) {
		this(() -> new QueueBufferPool<>(maxPoolSize, () -> bufferCreator.apply(bufferCapacity)));
	}

	@Override
	public void close(@NonNull B source) {
		BufferPool<B> bufferPool = threadLocal.get();
		if (bufferPool == null) {
			throw new IllegalStateException(
					"No buffer pool bound to current thread. close() called without prior get()?");
		}

		bufferPool.close(source);
	}

	@Override
	public B get() {
		BufferPool<B> bufferPool = threadLocal.get();
		if (bufferPool == null) {
			bufferPool = bufferPoolSupplier.get();
			threadLocal.set(bufferPool);
		}
		return bufferPool.get();
	}
}
package run.soeasy.framework.io.buffer;

import java.nio.Buffer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class NoneBufferPool<B extends Buffer> implements BufferPool<B> {
	@NonNull
	private final Supplier<? extends B> bufferCreater;

	public NoneBufferPool(int bufferSize, @NonNull IntFunction<? extends B> creator) {
		this(() -> creator.apply(bufferSize));
	}

	@Override
	public B get() {
		return bufferCreater.get();
	}

	@Override
	public void close(@NonNull B source) {
		// none
	}
}

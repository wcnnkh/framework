package run.soeasy.framework.io.buffer;

import java.nio.Buffer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pool;

public interface BufferPool<B extends Buffer> extends Pool<B, RuntimeException> {
	@Override
	B get();

	@Override
	void close(@NonNull B source);
}

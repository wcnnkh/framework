package run.soeasy.framework.io.buffer;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pool;

import java.nio.Buffer;

public interface BufferPool<B extends Buffer> extends Pool<B, RuntimeException> {
    @Override
    B get();

    @Override
    void close(@NonNull B source);
}

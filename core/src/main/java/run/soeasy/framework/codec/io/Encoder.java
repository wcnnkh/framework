package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.nio.Buffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferWriter;

@FunctionalInterface
public interface Encoder<T, B extends Buffer> {
	<E extends Throwable> void encode(T source, @NonNull BufferWriter<? super B, ? extends E> writer) throws IOException, E;
}

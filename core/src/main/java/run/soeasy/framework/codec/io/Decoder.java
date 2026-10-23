package run.soeasy.framework.codec.io;

import java.io.IOException;
import java.nio.Buffer;

import lombok.NonNull;
import run.soeasy.framework.io.buffer.BufferReader;

@FunctionalInterface
public interface Decoder<T, B extends Buffer> {
	<E extends Throwable> T decode(@NonNull BufferReader<? super B, ? extends E> source) throws IOException, E;
}

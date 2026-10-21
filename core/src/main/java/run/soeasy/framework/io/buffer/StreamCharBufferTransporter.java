package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

import lombok.NonNull;

public interface StreamCharBufferTransporter extends CharBufferTransporter {
	@Override
	default <R extends Throwable, W extends Throwable> void transfer(
			@NonNull BufferReader<? super CharBuffer, ? extends R> reader,
			@NonNull BufferWriter<? super CharBuffer, ? extends W> writer) throws IOException {
		try (Reader r = new CharBufferReader(reader); Writer w = new CharBufferWriter(writer)) {
			transfer(r, w);
		}
	}

	@Override
	void transfer(@NonNull Reader reader, @NonNull Writer writer) throws IOException;
}

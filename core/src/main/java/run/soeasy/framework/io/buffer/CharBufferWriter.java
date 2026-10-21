package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CharBufferWriter extends Writer {
	@NonNull
	private final BufferWriter<? super CharBuffer, ? extends Throwable> bufferWriter;

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		try {
			bufferWriter.write(CharBuffer.wrap(cbuf, off, len));
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			throw new IOException("Buffer writer", e);
		}
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
	}
}

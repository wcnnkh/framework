package run.soeasy.framework.io.buffer;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CharBufferReader extends Reader {
	@NonNull
	private final BufferReader<? super CharBuffer, ? extends Throwable> bufferReader;

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		try {
			return bufferReader.read(CharBuffer.wrap(cbuf, off, len));
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			throw new IOException("Buffer reader", e);
		}
	}

	@Override
	public void close() throws IOException {
	}
}

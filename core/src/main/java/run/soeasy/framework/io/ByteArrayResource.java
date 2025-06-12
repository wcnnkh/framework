package run.soeasy.framework.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

public final class ByteArrayResource extends ByteArrayOutputStream implements Resource {
	private int lastModified = 0;

	public ByteArrayResource() {
		super();
	}

	public ByteArrayResource(int initialSize) {
		super(initialSize);
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public byte[] readAllBytes() throws NoSuchElementException, IOException {
		return toByteArray();
	}

	@Override
	public String readAllCharacters() throws NoSuchElementException, IOException {
		return new String(buf, 0, count);
	}

	@Override
	public long contentLength() throws IOException {
		return count;
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(buf, 0, count);
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return this;
	}

	@Override
	public void flush() throws IOException {
		lastModified++;
		super.flush();
	}

	@Override
	public void close() throws IOException {
		try {
			flush();
		} finally {
			super.close();
		}
	}

	@Override
	public long lastModified() throws IOException {
		return lastModified;
	}
}

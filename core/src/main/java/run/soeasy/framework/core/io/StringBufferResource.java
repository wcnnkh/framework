package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.NoSuchElementException;

import lombok.Getter;

@Getter
public final class StringBufferResource extends StringWriter implements Resource {
	private long lastModified = 0;

	public StringBufferResource() {
		super();
	}

	public StringBufferResource(int initialSize) {
		super(initialSize);
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean isDecoded() {
		return true;
	}

	@Override
	public Reader getReader() throws IOException {
		return new StringReader(getBuffer().toString());
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	public boolean isEncoded() {
		return true;
	}

	@Override
	public Writer getWriter() throws IOException {
		return this;
	}

	@Override
	public long lastModified() throws IOException {
		return lastModified;
	}

	@Override
	public void flush() {
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
	public byte[] readAllBytes() throws NoSuchElementException, IOException {
		return readAllCharacters().getBytes();
	}

	@Override
	public String readAllCharacters() throws NoSuchElementException, IOException {
		return getBuffer().toString();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}
}

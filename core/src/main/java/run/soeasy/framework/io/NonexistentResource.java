package run.soeasy.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class NonexistentResource implements Resource {
	public static final NonexistentResource NONEXISTENT_RESOURCE = new NonexistentResource();

	@Override
	public InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public boolean isReadable() {
		return false;
	}

	@Override
	public boolean isWritable() {
		return false;
	}
}

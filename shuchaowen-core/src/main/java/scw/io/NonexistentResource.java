package scw.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import scw.lang.NotSupportedException;

public final class NonexistentResource implements Resource {

	public InputStream getInputStream() throws IOException {
		throw new NotSupportedException("empty resource");
	}

	public OutputStream getOutputStream() throws IOException {
		throw new NotSupportedException("empty resource");
	}

	public boolean exists() {
		return false;
	}

	public boolean isReadable() {
		return false;
	}

	public boolean isWritable() {
		return false;
	}

	public boolean isOpen() {
		return false;
	}

	public URL getURL() throws IOException {
		throw new NotSupportedException("empty resource");
	}

	public URI getURI() throws IOException {
		throw new NotSupportedException("empty resource");
	}

	public File getFile() throws IOException, FileNotFoundException {
		throw new NotSupportedException("empty resource");
	}

	public long contentLength() throws IOException {
		return 0;
	}

	public long lastModified() throws IOException {
		return 0;
	}

	public Resource createRelative(String relativePath) throws IOException {
		throw new NotSupportedException("empty resource");
	}

	public String getFilename() {
		throw new NotSupportedException("empty resource");
	}

	public String getDescription() {
		return "empty resource";
	}

}

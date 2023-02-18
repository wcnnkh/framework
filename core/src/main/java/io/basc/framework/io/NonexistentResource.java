package io.basc.framework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import io.basc.framework.event.EmptyObservable;
import io.basc.framework.lang.UnsupportedException;

public final class NonexistentResource extends EmptyObservable<Resource> implements Resource {
	public InputStream getInputStream() throws IOException {
		throw new UnsupportedException("empty resource");
	}

	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedException("empty resource");
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
		throw new UnsupportedException("empty resource");
	}

	public URI getURI() throws IOException {
		throw new UnsupportedException("empty resource");
	}

	public File getFile() throws IOException, FileNotFoundException {
		throw new UnsupportedException("empty resource");
	}

	public long contentLength() throws IOException {
		return 0;
	}

	public long lastModified() throws IOException {
		return 0;
	}

	public Resource createRelative(String relativePath) throws IOException {
		throw new UnsupportedException("empty resource");
	}

	public String getName() {
		throw new UnsupportedException("empty resource");
	}

	public String getDescription() {
		return "empty resource";
	}

	public boolean isSupportEventDispatcher() {
		return false;
	}
}

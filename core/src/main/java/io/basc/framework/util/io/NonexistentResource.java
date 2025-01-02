package io.basc.framework.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

import io.basc.framework.util.Pipeline;
import lombok.NonNull;

public class NonexistentResource implements Resource {
	public static final NonexistentResource INSTANCE = new NonexistentResource();

	@Override
	public @NonNull Pipeline<InputStream, IOException> getInputStream() {
		throw new UnsupportedOperationException("empty resource");
	}

	@Override
	public @NonNull Pipeline<OutputStream, IOException> getOutputStream() {
		throw new UnsupportedOperationException("empty resource");
	}

	public boolean exists() {
		return false;
	}

	public URL getURL() throws IOException {
		throw new UnsupportedOperationException("empty resource");
	}

	public URI getURI() throws IOException {
		throw new UnsupportedOperationException("empty resource");
	}

	public File getFile() throws IOException, FileNotFoundException {
		throw new UnsupportedOperationException("empty resource");
	}

	public long contentLength() throws IOException {
		return 0;
	}

	public long lastModified() throws IOException {
		return 0;
	}

	public Resource createRelative(String relativePath) throws IOException {
		throw new UnsupportedOperationException("empty resource");
	}

	public String getName() {
		throw new UnsupportedOperationException("empty resource");
	}

	public String getDescription() {
		return "empty resource";
	}
}

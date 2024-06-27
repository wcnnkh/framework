package io.basc.framework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import io.basc.framework.util.Wrapper;

public class ResourceWrapper<W extends Resource> extends Wrapper<W> implements Resource {

	public ResourceWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return wrappedTarget.getInputStream();
	}

	@Override
	public long lastModified() throws IOException {
		return wrappedTarget.lastModified();
	}

	@Override
	public boolean exists() {
		return wrappedTarget.exists();
	}

	@Override
	public URL getURL() throws IOException {
		return wrappedTarget.getURL();
	}

	@Override
	public URI getURI() throws IOException {
		return wrappedTarget.getURI();
	}

	@Override
	public File getFile() throws IOException, FileNotFoundException {
		return wrappedTarget.getFile();
	}

	@Override
	public long contentLength() throws IOException {
		return wrappedTarget.contentLength();
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {
		return wrappedTarget.createRelative(relativePath);
	}

	@Override
	public String getName() {
		return wrappedTarget.getName();
	}

	@Override
	public String getDescription() {
		return wrappedTarget.getDescription();
	}
}

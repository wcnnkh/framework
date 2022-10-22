package io.basc.framework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import io.basc.framework.event.support.StandardObservable;

public class Resources extends StandardObservable<Resource> implements Resource {

	@Override
	public InputStream getInputStream() throws IOException {
		return get().getInputStream();
	}

	@Override
	public boolean exists() {
		return isPresent();
	}

	@Override
	public URL getURL() throws IOException {
		return get().getURL();
	}

	@Override
	public URI getURI() throws IOException {
		return get().getURI();
	}

	@Override
	public File getFile() throws IOException, FileNotFoundException {
		return get().getFile();
	}

	@Override
	public long contentLength() throws IOException {
		return get().contentLength();
	}

	@Override
	public long lastModified() throws IOException {
		return get().lastModified();
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {
		return get().createRelative(relativePath);
	}

	@Override
	public String getName() {
		return get().getName();
	}

	@Override
	public String getDescription() {
		return get().getDescription();
	}
}

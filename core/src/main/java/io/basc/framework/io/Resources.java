package io.basc.framework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import io.basc.framework.event.support.ObservableResourceRegistry;

public class Resources extends ObservableResourceRegistry implements Resource {

	@Override
	public InputStream getInputStream() throws IOException {
		return getResource().getInputStream();
	}

	@Override
	public boolean exists() {
		return getResource().exists();
	}

	@Override
	public URL getURL() throws IOException {
		return getResource().getURL();
	}

	@Override
	public URI getURI() throws IOException {
		return getResource().getURI();
	}

	@Override
	public File getFile() throws IOException, FileNotFoundException {
		return getResource().getFile();
	}

	@Override
	public long contentLength() throws IOException {
		return getResource().contentLength();
	}

	@Override
	public long lastModified() throws IOException {
		return getResource().lastModified();
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {
		return getResource().createRelative(relativePath);
	}

	@Override
	public String getName() {
		return getResource().getName();
	}

	@Override
	public String getDescription() {
		return getResource().getDescription();
	}
}

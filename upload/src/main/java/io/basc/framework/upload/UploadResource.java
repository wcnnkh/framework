package io.basc.framework.upload;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import io.basc.framework.util.io.FileSystemResource;

public class UploadResource extends FileSystemResource{
	private final URI uri;
	
	public UploadResource(File file, URI uri) {
		super(file);
		this.uri = uri;
	}
	
	@Override
	public URI getURI() throws IOException {
		return this.uri;
	}

	@Override
	public URL getURL() throws IOException {
		return this.uri.toURL();
	}
}

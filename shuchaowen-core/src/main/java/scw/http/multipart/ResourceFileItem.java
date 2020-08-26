package scw.http.multipart;

import java.io.IOException;
import java.io.InputStream;

import scw.io.Resource;

class ResourceFileItem extends FileItem {
	private Resource resource;

	public ResourceFileItem(String fieldName, Resource resource) {
		super(fieldName);
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

	public InputStream getBody() throws IOException {
		return resource.getInputStream();
	}

	public void close() throws IOException {
		// ignore
	}

	@Override
	public String getName() {
		return resource.getFilename();
	}
}

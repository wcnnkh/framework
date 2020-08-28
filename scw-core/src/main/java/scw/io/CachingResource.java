package scw.io;

import java.io.IOException;
import java.io.InputStream;

public class CachingResource extends ResourceWrapper {
	private final byte[] data;
	private final Resource resource;

	public CachingResource(Resource resource, byte[] data) {
		this.resource = resource;
		this.data = data;
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new UnsafeByteArrayInputStream(data);
	}
}

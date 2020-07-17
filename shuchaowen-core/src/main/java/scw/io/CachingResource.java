package scw.io;

import java.io.IOException;
import java.io.InputStream;

public class CachingResource extends ResourceWrapper {
	private final byte[] data;

	public CachingResource(Resource resource, byte[] data) {
		super(resource);
		this.data = data;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new UnsafeByteArrayInputStream(data);
	}
}

package io.basc.framework.net.resource;

import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.net.Headers;
import io.basc.framework.net.InputMessage;
import io.basc.framework.util.io.Resource;
import lombok.Data;
import lombok.NonNull;

@Data
public class ResourceInputMessage implements InputMessage {
	@NonNull
	private final Resource resource;
	private final Headers headers = new Headers(true);

	@Override
	public InputStream getInputStream() throws IOException {
		return resource.getInputStream();
	}
}

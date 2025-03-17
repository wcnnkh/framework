package run.soeasy.framework.net.resource;

import java.io.IOException;
import java.io.InputStream;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.net.Headers;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.util.io.Resource;

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

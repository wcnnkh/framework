package io.basc.framework.net.multipart;

import io.basc.framework.net.Headers;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMultipartMessage implements MultipartMessage {
	private final Headers headers = new Headers(false);
	private final String name;

	public AbstractMultipartMessage(String name) {
		this.name = name;
	}

	@Override
	public long getSize() {
		return getContentLength();
	}
}

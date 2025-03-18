package run.soeasy.framework.messaging.multipart;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.messaging.Headers;

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

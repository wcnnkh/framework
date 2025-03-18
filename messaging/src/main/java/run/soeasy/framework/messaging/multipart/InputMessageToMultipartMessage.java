package run.soeasy.framework.messaging.multipart;

import java.io.IOException;
import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.messaging.Headers;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;

@Getter
@AllArgsConstructor
public class InputMessageToMultipartMessage implements MultipartMessage {
	@NonNull
	private final String name;
	private final String originalFilename;
	@NonNull
	private final InputMessage inputMessage;

	@Override
	public Headers getHeaders() {
		return inputMessage.getHeaders();
	}

	@Override
	public MediaType getContentType() {
		return inputMessage.getContentType();
	}

	@Override
	public long getContentLength() {
		return inputMessage.getContentLength();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return inputMessage.getInputStream();
	}

	@Override
	public long getSize() {
		return inputMessage.getContentLength();
	}

}

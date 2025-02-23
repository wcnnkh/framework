package io.basc.framework.net.multipart;

import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.net.Headers;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

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

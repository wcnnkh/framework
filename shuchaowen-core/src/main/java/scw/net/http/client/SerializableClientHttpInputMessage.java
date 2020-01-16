package scw.net.http.client;

import java.io.IOException;

import scw.io.IOUtils;
import scw.net.http.HttpHeaders;
import scw.net.http.HttpStatus;
import scw.net.http.MediaType;
import scw.net.message.SerializableInputMessage;

public class SerializableClientHttpInputMessage extends
		SerializableInputMessage implements ClientHttpInputMessage {
	private static final long serialVersionUID = 1L;
	private int rawStatusCode;
	private String statusText;

	public SerializableClientHttpInputMessage(
			ClientHttpResponse clientHttpResponse) throws IOException {
		super(IOUtils.toByteArray(clientHttpResponse.getBody()),
				clientHttpResponse.getHeaders());
		this.rawStatusCode = clientHttpResponse.getRawStatusCode();
		this.statusText = clientHttpResponse.getStatusText();
	}

	public HttpHeaders getHeaders() {
		return (HttpHeaders) super.getHeaders();
	}

	public HttpStatus getStatusCode() {
		return HttpStatus.valueOf(getRawStatusCode());
	}

	public int getRawStatusCode() {
		return rawStatusCode;
	}

	public String getStatusText() {
		return statusText;
	}

	@Override
	public MediaType getContentType() {
		return getHeaders().getContentType();
	}

	@Override
	public long getContentLength() {
		return getHeaders().getContentLength();
	}
}

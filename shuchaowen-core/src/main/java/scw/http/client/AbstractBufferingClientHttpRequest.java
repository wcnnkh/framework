package scw.http.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import scw.http.HttpHeaders;

abstract class AbstractBufferingClientHttpRequest extends AbstractClientHttpRequest {
	private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

	@Override
	protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
		return this.bufferedOutput;
	}

	@Override
	protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
		byte[] bytes = this.bufferedOutput.toByteArray();
		if (headers.getContentLength() < 0) {
			headers.setContentLength(bytes.length);
		}
		ClientHttpResponse result = executeInternal(headers, bytes);
		this.bufferedOutput = null;
		return result;
	}

	protected abstract ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput)
			throws IOException;
}

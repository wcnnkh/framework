package run.soeasy.framework.http.client;

import java.io.IOException;
import java.io.InputStream;

import run.soeasy.framework.http.client.ClientHttpResponse.ClientHttpResponseWrapper;
import run.soeasy.framework.util.function.Wrapped;
import run.soeasy.framework.util.io.IOUtils;
import run.soeasy.framework.util.io.UnsafeByteArrayInputStream;

public final class BufferingClientHttpResponseWrapper<W extends ClientHttpResponse> extends Wrapped<W>
		implements ClientHttpResponseWrapper<W> {
	private byte[] body;

	public BufferingClientHttpResponseWrapper(W source) {
		super(source);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (this.body == null) {
			body = IOUtils.copyToByteArray(source.getInputStream());
		}
		return new UnsafeByteArrayInputStream(body);
	}
}

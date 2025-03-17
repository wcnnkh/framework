package run.soeasy.framework.http.server;

import java.io.IOException;
import java.io.InputStream;

import run.soeasy.framework.http.server.ServerHttpRequest.ServerHttpRequestWrapper;
import run.soeasy.framework.util.function.Wrapped;
import run.soeasy.framework.util.io.IOUtils;
import run.soeasy.framework.util.io.UnsafeByteArrayInputStream;

/**
 * 缓存请求体，未处理内存溢出的情况
 * 
 * @author wcnnkh
 *
 */
public class CachingServerHttpRequest<W extends ServerHttpRequest> extends Wrapped<W>
		implements ServerHttpRequestWrapper<W> {
	private byte[] body;

	public CachingServerHttpRequest(W source) {
		super(source);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (body == null) {
			body = IOUtils.copyToByteArray(source.getInputStream());
		}
		return new UnsafeByteArrayInputStream(body);
	}
}

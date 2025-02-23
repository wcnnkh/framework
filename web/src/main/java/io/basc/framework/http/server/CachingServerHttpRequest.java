package io.basc.framework.http.server;

import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.http.server.ServerHttpRequest.ServerHttpRequestWrapper;
import io.basc.framework.util.function.Wrapped;
import io.basc.framework.util.io.IOUtils;
import io.basc.framework.util.io.UnsafeByteArrayInputStream;

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

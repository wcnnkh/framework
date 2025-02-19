package io.basc.framework.http.server;

import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.http.server.ServerHttpRequest.ServerHttpRequestWrapper;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.function.Wrapped;
import io.basc.framework.util.io.IOUtils;
import io.basc.framework.util.io.UnsafeByteArrayInputStream;
import lombok.NonNull;

/**
 * 缓存请求体，未处理内存溢出的情况
 * 
 * @author wcnnkh
 *
 */
public class CachingServerHttpRequest<W extends ServerHttpRequest> extends Wrapped<W>
		implements ServerHttpRequestWrapper<W> {
	private Pipeline<InputStream, IOException> inputStream;

	public CachingServerHttpRequest(W source) {
		super(source);
	}

	@Override
	public @NonNull Pipeline<InputStream, IOException> getInputStream() {
		if (inputStream == null) {
			inputStream = source.getInputStream().map((e) -> IOUtils.copyToByteArray(e))
					.map((e) -> new UnsafeByteArrayInputStream(e)).map((e) -> (InputStream) e).newPipeline();
		}
		return inputStream;
	}
}

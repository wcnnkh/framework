package io.basc.framework.web;

import io.basc.framework.http.HttpMethod;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.UnsafeByteArrayInputStream;
import io.basc.framework.util.XUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 缓存请求体，未处理内存溢出的情况
 * 
 * @author shuchaowen
 *
 */
public class CachingServerHttpRequest extends ServerHttpRequestWrapper {

	public CachingServerHttpRequest(ServerHttpRequest targetRequest) {
		super(targetRequest, true);
	}

	private byte[] bytes;

	public byte[] getBytes() throws IOException {
		CachingServerHttpRequest cachingServerHttpRequest = XUtils.getDelegate(wrappedTarget,
				CachingServerHttpRequest.class);
		if (cachingServerHttpRequest != null) {
			return cachingServerHttpRequest.getBytes();
		}

		if (getMethod() == HttpMethod.GET) {
			return null;
		}

		InputStream inputStream = super.getInputStream();
		if (inputStream == null) {
			return null;
		}

		if (bytes == null) {
			bytes = IOUtils.toByteArray(inputStream);
		}
		return bytes;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		byte[] bytes = getBytes();
		if (bytes == null) {
			return null;
		}
		return new UnsafeByteArrayInputStream(bytes);
	}
}

package io.basc.framework.servlet;

import java.net.URI;

import javax.servlet.ServletRequest;

import io.basc.framework.net.Headers;
import io.basc.framework.net.uri.UriUtils;

public class ServletServerRequestWrapper extends AbstractServletServerRequestWrepper<ServletRequest>{

	public ServletServerRequestWrapper(ServletRequest wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public URI getURI() {
		return UriUtils.toUri(source.getRequestURI());
	}

	@Override
	public Headers getHeaders() {
		return Headers.EMPTY;
	}

}

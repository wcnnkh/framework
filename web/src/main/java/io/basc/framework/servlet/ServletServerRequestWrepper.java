package io.basc.framework.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.InetSocketAddress;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import io.basc.framework.net.Headers;
import io.basc.framework.net.server.ServerAsyncControl;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerRequestDispatcher;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.function.Wrapped;
import io.basc.framework.util.function.Wrapper;
import io.basc.framework.util.io.ReaderSource;

public class ServletServerRequestWrepper<W extends ServletRequest> extends Wrapped<W> implements ServerRequest {
	private InetSocketAddress localAddress;
	private InetSocketAddress remoteAddress;
	private ServletServerAsyncControl serverAsyncControl;

	public ServletServerRequestWrepper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public void setAttribute(String name, Object value) {
		source.setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		source.removeAttribute(name);
	}

	@Override
	public Object getAttribute(String name) {
		return source.getAttribute(name);
	}

	@Override
	public Elements<String> getAttributeNames() {
		return Elements.of(() -> source.getAttributeNames());
	}

	@Override
	public long getContentLength() {
		return source.getContentLengthLong();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return source.getInputStream();
	}

	@Override
	public ReaderSource<Reader> toReaderFactory() {
		return () -> source.getReader();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		if (localAddress == null) {
			localAddress = new InetSocketAddress(this.source.getLocalName(), this.source.getLocalPort());
		}
		return localAddress;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		if (remoteAddress == null) {
			remoteAddress = new InetSocketAddress(this.source.getRemoteHost(), this.source.getRemotePort());
		}
		return remoteAddress;
	}

	@Override
	public String getProtocol() {
		return source.getProtocol();
	}

	@Override
	public String getScheme() {
		return source.getScheme();
	}

	@Override
	public boolean isSupportAsyncControl() {
		return source.isAsyncSupported();
	}

	@Override
	public ServerAsyncControl getAsyncControl(ServerResponse serverResponse) {
		if (serverAsyncControl == null) {
			ServletResponse servletResponse = Wrapper.unwrap(serverResponse, ServletResponse.class);
			if (servletResponse == null) {
				throw new IllegalArgumentException("Response must be a ServletResponse: " + serverResponse.getClass());
			}
			this.serverAsyncControl = new ServletServerAsyncControl(source, servletResponse);
		}
		return this.serverAsyncControl;
	}

	@Override
	public ServerRequestDispatcher getRequestDispatcher(String path) {
		RequestDispatcher requestDispatcher = source.getRequestDispatcher(path);
		return requestDispatcher == null ? null : new ServletRequestDispatcher(requestDispatcher);
	}

	@Override
	public Headers getHeaders() {
		return Headers.EMPTY;
	}
}

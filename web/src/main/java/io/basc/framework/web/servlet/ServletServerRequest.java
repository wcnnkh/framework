package io.basc.framework.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Enumeration;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import io.basc.framework.util.function.Wrapper;
import io.basc.framework.web.ServerAsyncControl;
import io.basc.framework.web.ServerRequest;
import io.basc.framework.web.ServerResponse;

public abstract class ServletServerRequest<W extends ServletRequest> extends Wrapper<W> implements ServerRequest {
	private InetSocketAddress localAddress;
	private InetSocketAddress remoteAddress;
	private ServletServerAsyncControl serverAsyncControl;

	public ServletServerRequest(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public void setAttribute(String name, Object value) {
		wrappedTarget.setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		wrappedTarget.removeAttribute(name);
	}

	@Override
	public Object getAttribute(String name) {
		return wrappedTarget.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return wrappedTarget.getAttributeNames();
	}

	@Override
	public long getContentLength() {
		return wrappedTarget.getContentLengthLong();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return wrappedTarget.getInputStream();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return wrappedTarget.getReader();
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		if (localAddress == null) {
			localAddress = new InetSocketAddress(this.wrappedTarget.getLocalName(), this.wrappedTarget.getLocalPort());
		}
		return localAddress;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		if (remoteAddress == null) {
			remoteAddress = new InetSocketAddress(this.wrappedTarget.getRemoteHost(),
					this.wrappedTarget.getRemotePort());
		}
		return remoteAddress;
	}

	@Override
	public String getProtocol() {
		return wrappedTarget.getProtocol();
	}

	@Override
	public String getScheme() {
		return wrappedTarget.getScheme();
	}

	@Override
	public boolean isSupportAsyncControl() {
		return wrappedTarget.isAsyncSupported();
	}

	@Override
	public ServerAsyncControl getAsyncControl(ServerResponse serverResponse) {
		if (serverAsyncControl == null) {
			if (serverResponse instanceof ServletServerResponse) {
				ServletServerResponse<?> servletServerResponse = (ServletServerResponse<?>) serverResponse;
				this.serverAsyncControl = new ServletServerAsyncControl(wrappedTarget,
						servletServerResponse.getDelegateSource());
			} else {
				throw new IllegalArgumentException(
						"Response must be a ServletServerHttpResponse: " + serverResponse.getClass());
			}
		}
		return this.serverAsyncControl;
	}

}

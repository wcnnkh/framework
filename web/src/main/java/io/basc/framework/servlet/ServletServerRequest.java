package io.basc.framework.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.InetSocketAddress;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import io.basc.framework.net.server.ServerAsyncControl;
import io.basc.framework.net.server.ServerRequest;
import io.basc.framework.net.server.ServerResponse;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.function.Wrapped;
import io.basc.framework.util.function.Wrapper;
import lombok.NonNull;

public abstract class ServletServerRequest<W extends ServletRequest> extends Wrapped<W> implements ServerRequest {
	private InetSocketAddress localAddress;
	private InetSocketAddress remoteAddress;
	private ServletServerAsyncControl serverAsyncControl;

	public ServletServerRequest(W wrappedTarget) {
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

	private Pipeline<InputStream, IOException> inputStream;

	@Override
	public @NonNull Pipeline<InputStream, IOException> getInputStream() {
		if (inputStream == null) {
			inputStream = Pipeline.of(() -> source.getInputStream());
		}
		return inputStream;
	}

	private Pipeline<Reader, IOException> reader;

	@Override
	public @NonNull Pipeline<Reader, IOException> getReader() {
		// TODO Auto-generated method stub
		return ServerRequest.super.getReader();
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

}

package io.basc.framework.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.Enumeration;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.MediaType;
import io.basc.framework.lang.Constants;
import io.basc.framework.net.message.InputMessageWrapper;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.collect.MultiValueMap;

public class ServerHttpRequestWrapper extends InputMessageWrapper<ServerHttpRequest> implements ServerHttpRequest {
	private final boolean overrideBody;

	public ServerHttpRequestWrapper(ServerHttpRequest targetRequest) {
		this(targetRequest, false);
	}

	public ServerHttpRequestWrapper(ServerHttpRequest targetRequest, boolean overrideBody) {
		super(targetRequest);
		this.overrideBody = overrideBody;
	}

	public final boolean isOverrideBody() {
		return overrideBody;
	}

	private BufferedReader reader;

	public BufferedReader getReader() throws IOException {
		if (isOverrideBody()) {
			if (reader == null) {
				InputStream inputStream = getInputStream();
				if (inputStream == null) {
					return null;
				}

				String charsetName = getCharacterEncoding();
				if (charsetName == null) {
					charsetName = Constants.UTF_8.name();
				}
				reader = new BufferedReader(new InputStreamReader(inputStream, charsetName));
			}
			return reader;
		}
		return wrappedTarget.getReader();
	}

	public InetSocketAddress getLocalAddress() {
		return wrappedTarget.getLocalAddress();
	}

	public InetSocketAddress getRemoteAddress() {
		return wrappedTarget.getRemoteAddress();
	}

	public MultiValueMap<String, String> getParameterMap() {
		return wrappedTarget.getParameterMap();
	}

	public Principal getPrincipal() {
		return wrappedTarget.getPrincipal();
	}

	public void setAttribute(String name, Object o) {
		wrappedTarget.setAttribute(name, o);
	}

	public void removeAttribute(String name) {
		wrappedTarget.removeAttribute(name);
	}

	public Object getAttribute(String name) {
		return wrappedTarget.getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		return wrappedTarget.getAttributeNames();
	}

	public String getPath() {
		return wrappedTarget.getPath();
	}

	public String getContextPath() {
		return wrappedTarget.getContextPath();
	}

	public HttpHeaders getHeaders() {
		return wrappedTarget.getHeaders();
	}

	public MediaType getContentType() {
		return wrappedTarget.getContentType();
	}

	public HttpMethod getMethod() {
		return wrappedTarget.getMethod();
	}

	public URI getURI() {
		return wrappedTarget.getURI();
	}

	public String getRawMethod() {
		return wrappedTarget.getRawMethod();
	}

	public HttpCookie[] getCookies() {
		return wrappedTarget.getCookies();
	}

	public Session getSession() {
		return wrappedTarget.getSession();
	}

	public Session getSession(boolean create) {
		return wrappedTarget.getSession(create);
	}

	public boolean isSupportAsyncControl() {
		return wrappedTarget.isSupportAsyncControl();
	}

	@Override
	public ServerAsyncControl getAsyncControl(ServerResponse serverResponse) {
		return wrappedTarget.getAsyncControl(serverResponse);
	}

	public String getIp() {
		return wrappedTarget.getIp();
	}

	@Override
	public String toString() {
		return wrappedTarget.toString();
	}

	@Override
	public int hashCode() {
		return wrappedTarget.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof ServerHttpRequestWrapper) {
			return ObjectUtils.equals(wrappedTarget, ((ServerHttpRequestWrapper) obj).wrappedTarget);
		}
		return false;
	}

	@Override
	public String getProtocol() {
		return wrappedTarget.getProtocol();
	}

	@Override
	public String getScheme() {
		return wrappedTarget.getScheme();
	}
}

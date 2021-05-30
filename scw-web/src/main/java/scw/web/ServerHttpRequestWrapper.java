package scw.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.Enumeration;

import scw.core.Constants;
import scw.core.utils.ObjectUtils;
import scw.http.HttpCookie;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.MediaType;
import scw.security.session.Session;
import scw.util.MultiValueMap;
import scw.util.Decorator;
import scw.util.XUtils;

public class ServerHttpRequestWrapper implements ServerHttpRequest, Decorator {
	private final ServerHttpRequest targetRequest;
	private final boolean overrideBody;

	public ServerHttpRequestWrapper(ServerHttpRequest targetRequest) {
		this(targetRequest, false);
	}

	public ServerHttpRequestWrapper(ServerHttpRequest targetRequest, boolean overrideBody) {
		this.targetRequest = targetRequest;
		this.overrideBody = overrideBody;
	}

	public final boolean isOverrideBody() {
		return overrideBody;
	}

	public ServerHttpRequest getTargetRequest() {
		return targetRequest;
	}

	public <T> T getDelegate(Class<T> targetType) {
		return XUtils.getDelegate(targetRequest, targetType);
	}

	public InputStream getBody() throws IOException {
		return targetRequest.getBody();
	}

	public long getContentLength() {
		return targetRequest.getContentLength();
	}

	public String getCharacterEncoding() {
		return targetRequest.getCharacterEncoding();
	}

	private BufferedReader reader;

	public BufferedReader getReader() throws IOException {
		if (isOverrideBody()) {
			if (reader == null) {
				InputStream inputStream = getBody();
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
		return targetRequest.getReader();
	}

	public InetSocketAddress getLocalAddress() {
		return targetRequest.getLocalAddress();
	}

	public InetSocketAddress getRemoteAddress() {
		return targetRequest.getRemoteAddress();
	}

	public MultiValueMap<String, String> getParameterMap() {
		return targetRequest.getParameterMap();
	}

	public Principal getPrincipal() {
		return targetRequest.getPrincipal();
	}

	public void setAttribute(String name, Object o) {
		targetRequest.setAttribute(name, o);
	}

	public void removeAttribute(String name) {
		targetRequest.removeAttribute(name);
	}

	public Object getAttribute(String name) {
		return targetRequest.getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		return targetRequest.getAttributeNames();
	}

	public String getPath() {
		return targetRequest.getPath();
	}

	public String getContextPath() {
		return targetRequest.getContextPath();
	}

	public HttpHeaders getHeaders() {
		return targetRequest.getHeaders();
	}

	public MediaType getContentType() {
		return targetRequest.getContentType();
	}

	public HttpMethod getMethod() {
		return targetRequest.getMethod();
	}

	public URI getURI() {
		return targetRequest.getURI();
	}

	public String getRawMethod() {
		return targetRequest.getRawMethod();
	}

	public HttpCookie[] getCookies() {
		return targetRequest.getCookies();
	}

	public Session getSession() {
		return targetRequest.getSession();
	}

	public Session getSession(boolean create) {
		return targetRequest.getSession(create);
	}

	public boolean isSupportAsyncControl() {
		return targetRequest.isSupportAsyncControl();
	}

	public ServerHttpAsyncControl getAsyncControl(ServerHttpResponse response) {
		return targetRequest.getAsyncControl(response);
	}

	public String getIp() {
		return targetRequest.getIp();
	}

	@Override
	public String toString() {
		return targetRequest.toString();
	}

	@Override
	public int hashCode() {
		return targetRequest.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof ServerHttpRequestWrapper) {
			return ObjectUtils.nullSafeEquals(targetRequest, ((ServerHttpRequestWrapper) obj).targetRequest);
		}
		return false;
	}
}

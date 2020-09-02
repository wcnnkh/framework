package scw.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.Principal;
import java.util.Enumeration;

import scw.core.Target;
import scw.core.utils.XUtils;
import scw.http.HttpCookie;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.MediaType;
import scw.net.InetAddress;
import scw.security.session.Session;
import scw.util.MultiValueMap;

public class ServerHttpRequestWrapper implements ServerHttpRequest, Target {
	private final ServerHttpRequest targetRequest;

	public ServerHttpRequestWrapper(ServerHttpRequest targetRequest) {
		this.targetRequest = targetRequest;
	}

	public ServerHttpRequest getTargetRequest() {
		return targetRequest;
	}
	
	public <T> T getTarget(Class<T> targetType) {
		return XUtils.getTarget(targetRequest, targetType);
	}

	public InputStream getBody() throws IOException {
		return targetRequest.getBody();
	}

	public long getContentLength() {
		return targetRequest.getContentLength();
	}

	public String getPath() {
		return targetRequest.getPath();
	}

	public String getContextPath() {
		return targetRequest.getContextPath();
	}

	public String getCharacterEncoding() {
		return targetRequest.getCharacterEncoding();
	}

	public BufferedReader getReader() throws IOException {
		return targetRequest.getReader();
	}

	public InetAddress getLocalAddress() {
		return targetRequest.getLocalAddress();
	}

	public InetAddress getRemoteAddress() {
		return targetRequest.getRemoteAddress();
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

	public MultiValueMap<String, String> getParameterMap() {
		return targetRequest.getParameterMap();
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

	public boolean isSupportAsyncControl() {
		return targetRequest.isSupportAsyncControl();
	}

	public ServerHttpAsyncControl getAsyncControl(ServerHttpResponse response) {
		return targetRequest.getAsyncControl(response);
	}

	public String getIp() {
		return targetRequest.getIp();
	}

	public MultiValueMap<String, String> getRestfulParameterMap() {
		return targetRequest.getRestfulParameterMap();
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
		return targetRequest.equals(obj);
	}
}

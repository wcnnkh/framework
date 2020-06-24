package scw.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.util.Enumeration;

import scw.http.HttpCookie;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.MediaType;
import scw.net.InetAddress;
import scw.net.RestfulParameterMapAware;
import scw.security.session.Session;
import scw.util.MultiValueMap;

public class ServerHttpRequestWrapper implements ServerHttpRequest, RestfulParameterMapAware {
	protected final ServerHttpRequest targetRequest;

	public ServerHttpRequestWrapper(ServerHttpRequest targetRequest) {
		this.targetRequest = targetRequest;
	}

	public ServerHttpRequest getTargetRequest() {
		if(targetRequest instanceof ServerHttpRequestWrapper){
			return ((ServerHttpRequestWrapper) targetRequest).getTargetRequest();
		}
		return targetRequest;
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

	public String getRawContentType() {
		return targetRequest.getRawContentType();
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
	
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
		targetRequest.setCharacterEncoding(env);
	}

	public void setRestfulParameterMap(MultiValueMap<String, String> parameterMap) {
		if(targetRequest instanceof RestfulParameterMapAware){
			((RestfulParameterMapAware) targetRequest).setRestfulParameterMap(parameterMap);
		}
	}

	public MultiValueMap<String, String> getRestfulParameterMap() {
		return targetRequest.getRestfulParameterMap();
	}
}

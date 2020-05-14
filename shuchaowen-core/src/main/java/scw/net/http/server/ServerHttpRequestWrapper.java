package scw.net.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Map;

import scw.net.http.HttpCookie;
import scw.net.http.HttpHeaders;
import scw.net.http.HttpMethod;
import scw.net.http.MediaType;
import scw.security.session.Session;

public class ServerHttpRequestWrapper implements ServerHttpRequest {
	protected final ServerHttpRequest targetRequest;

	public ServerHttpRequestWrapper(ServerHttpRequest targetRequest) {
		this.targetRequest = targetRequest;
	}
	
	public InputStream getBody() throws IOException {
		return targetRequest.getBody();
	}

	public long getContentLength() {
		return targetRequest.getContentLength();
	}

	public String getController() {
		return targetRequest.getController();
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

	public InetSocketAddress getLocalAddress() {
		return targetRequest.getLocalAddress();
	}

	public InetSocketAddress getRemoteAddress() {
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

	public String getParameter(String name) {
		return targetRequest.getParameter(name);
	}
	
	public Enumeration<String> getParameterNames() {
		return targetRequest.getParameterNames();
	}
	
	public Map<String, String[]> getParameterMap() {
		return targetRequest.getParameterMap();
	}
	
	public String[] getParameterValues(String name) {
		return targetRequest.getParameterValues(name);
	}

	public String getIP() {
		return targetRequest.getIP();
	}

	public String getRawMethod() {
		return targetRequest.getRawMethod();
	}

	public HttpCookie getCookie(String name) {
		return targetRequest.getCookie(name);
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
}

package scw.mvc.http;

import java.net.URI;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Map;

import scw.mvc.ServerRequestWrapper;
import scw.net.http.HttpCookie;
import scw.net.http.HttpHeaders;
import scw.net.http.HttpMethod;
import scw.net.http.MediaType;
import scw.security.session.Session;

public class ServerHttpRequestWrapper extends ServerRequestWrapper<ServerHttpRequest> implements ServerHttpRequest {

	public ServerHttpRequestWrapper(ServerHttpRequest targetRequest) {
		super(targetRequest);
	}

	public HttpHeaders getHeaders() {
		return targetRequest.getHeaders();
	}

	@Override
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

	public String[] getParameterValues(String name) {
		return targetRequest.getParameterValues(name);
	}

	public Map<String, String[]> getParameterMap() {
		return targetRequest.getParameterMap();
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

	public Session getHttpSession() {
		return targetRequest.getHttpSession();
	}

	public Session getHttpSession(boolean create) {
		return targetRequest.getHttpSession(create);
	}

	public Principal getPrincipal() {
		return targetRequest.getPrincipal();
	}
}

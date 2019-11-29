package scw.mvc.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

import scw.core.attribute.AttributesWrapper;
import scw.net.http.Cookie;
import scw.security.session.Session;

public class HttpRequestWrapper extends AttributesWrapper<String, Object> implements HttpRequest {
	private final HttpRequest targetHttpRequest;

	public HttpRequestWrapper(HttpRequest httpRequest) {
		super(httpRequest);
		this.targetHttpRequest = httpRequest;
	}

	public final HttpRequest getTargetHtpRequest() {
		return targetHttpRequest;
	}

	public String getCharacterEncoding() {
		return targetHttpRequest.getCharacterEncoding();
	}

	public void setCharacterEncoding(String env){
		targetHttpRequest.setCharacterEncoding(env);
	}

	public InputStream getInputStream() throws IOException {
		return targetHttpRequest.getInputStream();
	}

	public BufferedReader getReader() throws IOException {
		return targetHttpRequest.getReader();
	}

	public String getMethod() {
		return targetHttpRequest.getMethod();
	}

	public String getRequestPath() {
		return targetHttpRequest.getRequestPath();
	}

	public Cookie getCookie(String name, boolean ignoreCase) {
		return targetHttpRequest.getCookie(name, ignoreCase);
	}

	public long getDateHeader(String name) {
		return targetHttpRequest.getDateHeader(name);
	}

	public String getHeader(String name) {
		return targetHttpRequest.getHeader(name);
	}

	public Enumeration<String> getHeaders(String name) {
		return targetHttpRequest.getHeaders(name);
	}

	public Enumeration<String> getHeaderNames() {
		return targetHttpRequest.getHeaderNames();
	}

	public int getIntHeader(String name) {
		return targetHttpRequest.getIntHeader(name);
	}

	public Session getHttpSession() {
		return targetHttpRequest.getHttpSession();
	}

	public Session getHttpSession(boolean create) {
		return targetHttpRequest.getHttpSession(create);
	}

	public String getContentType() {
		return targetHttpRequest.getContentType();
	}

	public String getParameter(String name) {
		return targetHttpRequest.getParameter(name);
	}

	public Enumeration<String> getParameterNames() {
		return targetHttpRequest.getParameterNames();
	}

	public String[] getParameterValues(String name) {
		return targetHttpRequest.getParameterValues(name);
	}

	public Map<String, String[]> getParameterMap() {
		return targetHttpRequest.getParameterMap();
	}

	public String getRemoteAddr() {
		return targetHttpRequest.getRemoteAddr();
	}

	public String getRemoteHost() {
		return targetHttpRequest.getRemoteHost();
	}

	public String getIP() {
		return targetHttpRequest.getIP();
	}

	public boolean isAjax() {
		return targetHttpRequest.isAjax();
	}

	public String getContextPath() {
		return targetHttpRequest.getContextPath();
	}

}

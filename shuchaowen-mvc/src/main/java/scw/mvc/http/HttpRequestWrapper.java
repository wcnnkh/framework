package scw.mvc.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.Map;

import scw.net.http.Cookie;
import scw.net.http.HttpHeaders;
import scw.net.http.MediaType;
import scw.net.http.Method;
import scw.net.message.converter.MessageConvertException;
import scw.security.session.Session;

public class HttpRequestWrapper implements HttpRequest {
	private final HttpRequest targetHttpRequest;

	public HttpRequestWrapper(HttpRequest httpRequest) {
		this.targetHttpRequest = httpRequest;
	}

	public final HttpRequest getTargetHtpRequest() {
		return targetHttpRequest;
	}

	public String getCharacterEncoding() {
		return targetHttpRequest.getCharacterEncoding();
	}

	public void setCharacterEncoding(String env) {
		targetHttpRequest.setCharacterEncoding(env);
	}

	public InputStream getBody() throws IOException {
		return targetHttpRequest.getBody();
	}

	public BufferedReader getReader() throws IOException {
		return targetHttpRequest.getReader();
	}

	public String getRawMethod() {
		return targetHttpRequest.getRawMethod();
	}

	public Method getMethod() {
		return targetHttpRequest.getMethod();
	}

	public Cookie getCookie(String name, boolean ignoreCase) {
		return targetHttpRequest.getCookie(name, ignoreCase);
	}

	public Session getHttpSession() {
		return targetHttpRequest.getHttpSession();
	}

	public Session getHttpSession(boolean create) {
		return targetHttpRequest.getHttpSession(create);
	}

	public MediaType getContentType() {
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

	public HttpHeaders getHeaders() {
		return targetHttpRequest.getHeaders();
	}

	public URI getURI() {
		return targetHttpRequest.getURI();
	}

	public String getHeader(String name) {
		return targetHttpRequest.getHeader(name);
	}

	public Enumeration<String> getHeaderNames() {
		return targetHttpRequest.getHeaderNames();
	}

	public Enumeration<String> getHeaders(String name) {
		return targetHttpRequest.getHeaders(name);
	}

	public long getContentLength() {
		return targetHttpRequest.getContentLength();
	}

	public byte[] toByteArray() throws IOException {
		return targetHttpRequest.toByteArray();
	}

	public String convertToString(String charsetName) throws IOException, MessageConvertException {
		return targetHttpRequest.convertToString(charsetName);
	}

	public String convertToString() throws IOException, MessageConvertException {
		return targetHttpRequest.convertToString();
	}

	public String getControllerPath() {
		return targetHttpRequest.getControllerPath();
	}

	public String getRawContentType() {
		return targetHttpRequest.getRawContentType();
	}
}

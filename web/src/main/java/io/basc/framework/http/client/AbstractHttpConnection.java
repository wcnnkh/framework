package io.basc.framework.http.client;

import java.net.CookieHandler;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.net.MediaType;

public abstract class AbstractHttpConnection implements HttpConnection {
	private ClientHttpRequestFactory requestFactory;
	private CookieHandler cookieHandler;
	private RedirectManager redirectManager;
	private String httpMethod;
	private URI uri;
	private final HttpHeaders headers;
	private Object body;
	private TypeDescriptor typeDescriptor;
	private boolean cloneBeforeSet;

	public AbstractHttpConnection(URI uri, String httpMethod) {
		this.uri = uri;
		this.httpMethod = httpMethod;
		this.headers = new HttpHeaders();
	}

	public AbstractHttpConnection(AbstractHttpConnection connection) {
		this.requestFactory = connection.requestFactory;
		this.cookieHandler = connection.cookieHandler;
		this.redirectManager = connection.redirectManager;
		this.httpMethod = connection.httpMethod;
		this.uri = connection.uri;
		this.headers = new HttpHeaders(connection.headers);
		this.body = connection.body;
		this.typeDescriptor = connection.typeDescriptor;
		this.cloneBeforeSet = connection.cloneBeforeSet;
	}

	public boolean isCloneBeforeSet() {
		return cloneBeforeSet;
	}

	public void setCloneBeforeSet(boolean cloneBeforeSet) {
		this.cloneBeforeSet = cloneBeforeSet;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public URI getURI() {
		return uri;
	}

	@Override
	public String getRawMethod() {
		return httpMethod;
	}

	@Override
	public abstract AbstractHttpConnection clone();

	public HttpConnection header(String headerName, String... headerValues) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		for (String headerValue : headerValues) {
			connection.headers.add(headerName, headerValue);
		}
		return connection;
	}

	public HttpConnection headers(HttpHeaders headers) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		if (headers != null) {
			connection.headers.putAll(headers);
		}
		return connection;
	}

	public HttpConnection accept(MediaType... acceptableMediaTypes) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		connection.headers.setAccept(Arrays.asList(acceptableMediaTypes));
		return connection;
	}

	public HttpConnection acceptCharset(Charset... acceptableCharsets) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		connection.headers.setAcceptCharset(Arrays.asList(acceptableCharsets));
		return connection;
	}

	public HttpConnection contentLength(long contentLength) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		connection.headers.setContentLength(contentLength);
		return connection;
	}

	public HttpConnection contentType(MediaType contentType) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		connection.headers.setContentType(contentType);
		return connection;
	}

	public HttpConnection ifNoneMatch(String... ifNoneMatches) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		connection.headers.setIfNoneMatch(Arrays.asList(ifNoneMatches));
		return connection;
	}

	public HttpConnection ifModifiedSince(long ifModifiedSince) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		connection.headers.setIfModifiedSince(ifModifiedSince);
		return connection;
	}

	public HttpConnection body(Object body, TypeDescriptor typeDescriptor) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		connection.body = body;
		connection.typeDescriptor = typeDescriptor;
		return connection;
	}

	public Object getBody() {
		return body;
	}

	public TypeDescriptor getTypeDescriptor() {
		if (typeDescriptor == null && body != null) {
			return TypeDescriptor.forObject(body);
		}
		return typeDescriptor;
	}

	@Override
	public CookieHandler getCookieHandler() {
		return cookieHandler;
	}

	@Override
	public HttpConnection setCookieHandler(CookieHandler cookieHandler) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		connection.cookieHandler = cookieHandler;
		return connection;
	}

	@Override
	public RedirectManager getRedirectManager() {
		return redirectManager;
	}

	@Override
	public HttpConnection setRedirectManager(RedirectManager redirectManager) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		connection.redirectManager = redirectManager;
		return connection;
	}

	@Override
	public ClientHttpRequestFactory getRequestFactory() {
		return requestFactory;
	}

	@Override
	public HttpConnection setRequestFactory(ClientHttpRequestFactory requestFactory) {
		AbstractHttpConnection connection = isCloneBeforeSet() ? clone() : this;
		connection.requestFactory = requestFactory;
		return connection;
	}
}
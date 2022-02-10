package io.basc.framework.http.client;

import java.net.CookieHandler;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpRequestEntity;
import io.basc.framework.http.MediaType;
import io.basc.framework.lang.Nullable;

public abstract class AbstractHttpConnection extends AbstractHttpConnectionFactory
		implements HttpConnection, Cloneable {
	static final RedirectManager REDIRECT_MANAGER = Sys.env
			.getServiceLoader(RedirectManager.class, DefaultHttpRedirectManager.class).first();
	private HttpMethod method;
	private URI uri;
	private final HttpHeaders headers;
	@Nullable
	private Object body;
	@Nullable
	private TypeDescriptor typeDescriptor;
	private boolean redirectEnable = false;

	public AbstractHttpConnection(AbstractHttpConnectionFactory connectionFactory) {
		super(connectionFactory);
		this.headers = new HttpHeaders();
	}

	public AbstractHttpConnection(AbstractHttpConnectionFactory connectionFactory, HttpMethod method, URI uri) {
		super(connectionFactory);
		this.headers = new HttpHeaders();
		this.method = method;
		this.uri = uri;
	}

	public AbstractHttpConnection(AbstractHttpConnection httpConnection) {
		super(httpConnection);
		this.method = httpConnection.method;
		this.uri = httpConnection.uri;
		this.body = httpConnection.body;
		this.typeDescriptor = httpConnection.typeDescriptor;
		this.headers = new HttpHeaders(httpConnection.headers);
		this.redirectEnable = httpConnection.redirectEnable;
	}

	@Override
	public AbstractHttpConnection setRequestFactory(ClientHttpRequestFactory clientHttpRequestFactory) {
		super.setRequestFactory(clientHttpRequestFactory);
		return this;
	}

	public AbstractHttpConnection setRedirectManager(RedirectManager redirectManager) {
		super.setRedirectManager(redirectManager);
		return this;
	}

	@Override
	public AbstractHttpConnection setCookieHandler(CookieHandler cookieHandler) {
		super.setCookieHandler(cookieHandler);
		return this;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public URI getURI() {
		return uri;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public HttpConnection createConnection() {
		if (uri == null && method == null) {
			return this;
		}

		return clone();
	}

	public HttpConnection createConnection(HttpMethod method, URI uri) {
		if (this.method != null || this.uri != null) {
			// 创建一个新的
			AbstractHttpConnection connection = clone();
			connection.method = method;
			connection.uri = uri;
			return connection;
		}
		this.method = method;
		this.uri = uri;
		return this;
	}

	public boolean isRedirectEnable() {
		return redirectEnable;
	}

	public HttpConnection setRedirectEnable(boolean enable) {
		this.redirectEnable = enable;
		return this;
	}

	public HttpConnection header(String headerName, String... headerValues) {
		for (String headerValue : headerValues) {
			this.headers.add(headerName, headerValue);
		}
		return this;
	}

	public HttpConnection headers(@Nullable HttpHeaders headers) {
		if (headers != null) {
			this.headers.putAll(headers);
		}
		return this;
	}

	public HttpConnection accept(MediaType... acceptableMediaTypes) {
		this.headers.setAccept(Arrays.asList(acceptableMediaTypes));
		return this;
	}

	public HttpConnection acceptCharset(Charset... acceptableCharsets) {
		this.headers.setAcceptCharset(Arrays.asList(acceptableCharsets));
		return this;
	}

	public HttpConnection contentLength(long contentLength) {
		this.headers.setContentLength(contentLength);
		return this;
	}

	public HttpConnection contentType(MediaType contentType) {
		this.headers.setContentType(contentType);
		return this;
	}

	public HttpConnection ifNoneMatch(String... ifNoneMatches) {
		this.headers.setIfNoneMatch(Arrays.asList(ifNoneMatches));
		return this;
	}

	public HttpConnection ifModifiedSince(long ifModifiedSince) {
		this.headers.setIfModifiedSince(ifModifiedSince);
		return this;
	}

	public HttpConnection body(Object body) {
		this.body = body;
		return this;
	}

	public HttpConnection body(Object body, TypeDescriptor typeDescriptor) {
		this.body = body;
		this.typeDescriptor = typeDescriptor;
		return this;
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

	@SuppressWarnings("unchecked")
	public <T> HttpRequestEntity<T> buildRequestEntity() {
		return (HttpRequestEntity<T>) HttpRequestEntity.method(getMethod(), getURI()).headers(getHeaders())
				.body(getBody(), getTypeDescriptor());
	}

	@Override
	public AbstractHttpConnection clone() {
		try {
			return (AbstractHttpConnection) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
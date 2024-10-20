package io.basc.framework.http;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.ObjectUtils;

public class HttpRequestEntity<T> extends HttpEntity<T> implements HttpRequest {
	private static final long serialVersionUID = 1L;

	@Nullable
	private final String method;

	private final URI url;

	public HttpRequestEntity(String method, URI url) {
		this(null, null, method, url, null);
	}

	public HttpRequestEntity(@Nullable T body, String method, URI url) {
		this(body, null, method, url, null);
	}

	public HttpRequestEntity(@Nullable T body, String method, URI url, TypeDescriptor typeDescriptor) {
		this(body, null, method, url, typeDescriptor);
	}

	public HttpRequestEntity(MultiValueMap<String, String> headers, String method, URI url) {
		this(null, headers, method, url, null);
	}

	public HttpRequestEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, @Nullable String method,
			URI url) {
		this(body, headers, method, url, null);
	}

	public HttpRequestEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, @Nullable String method,
			URI url, @Nullable TypeDescriptor typeDescriptor) {
		super(body, typeDescriptor, headers);
		this.method = method;
		this.url = url;
	}

	@Nullable
	@Override
	public String getRawMethod() {
		return this.method;
	}

	@Override
	public final URI getURI() {
		return this.url;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other)) {
			return false;
		}

		if (other instanceof HttpRequestEntity) {
			HttpRequestEntity<?> otherEntity = (HttpRequestEntity<?>) other;
			return (ObjectUtils.equals(getMethod(), otherEntity.getMethod())
					&& ObjectUtils.equals(getURI(), otherEntity.getURI()));
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = super.hashCode();
		hashCode = 29 * hashCode + ObjectUtils.hashCode(this.method);
		hashCode = 29 * hashCode + ObjectUtils.hashCode(this.url);
		return hashCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("<");
		builder.append(getMethod());
		builder.append(' ');
		builder.append(getURI());
		builder.append(',');
		T body = getBody();
		HttpHeaders headers = getHeaders();
		if (body != null) {
			builder.append(body);
			builder.append(',');
		}
		builder.append(headers);
		builder.append('>');
		return builder.toString();
	}

	// Static builder methods

	/**
	 * Create a builder with the given method and url.
	 * 
	 * @param method the HTTP method (GET, POST, etc)
	 * @param url    the URL
	 * @return the created builder
	 */
	public static BodyBuilder<?> method(HttpMethod method, URI url) {
		return new DefaultBodyBuilder(method, url);
	}

	public static BodyBuilder<?> method(HttpMethod method, String url) {
		return method(method, UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP GET builder with the given url.
	 * 
	 * @param url the URL
	 * @return the created builder
	 */
	public static HeadersBuilder<?> get(URI url) {
		return method(HttpMethod.GET, url);
	}

	public static HeadersBuilder<?> get(String url) {
		return get(UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP HEAD builder with the given url.
	 * 
	 * @param url the URL
	 * @return the created builder
	 */
	public static HeadersBuilder<?> head(URI url) {
		return method(HttpMethod.HEAD, url);
	}

	public static HeadersBuilder<?> head(String url) {
		return head(UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP POST builder with the given url.
	 * 
	 * @param url the URL
	 * @return the created builder
	 */
	public static BodyBuilder<?> post(URI url) {
		return method(HttpMethod.POST, url);
	}

	public static BodyBuilder<?> post(String url) {
		return post(UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP PUT builder with the given url.
	 * 
	 * @param url the URL
	 * @return the created builder
	 */
	public static BodyBuilder<?> put(URI url) {
		return method(HttpMethod.PUT, url);
	}

	public static BodyBuilder<?> put(String url) {
		return put(UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP PATCH builder with the given url.
	 * 
	 * @param url the URL
	 * @return the created builder
	 */
	public static BodyBuilder<?> patch(URI url) {
		return method(HttpMethod.PATCH, url);
	}

	public static BodyBuilder<?> patch(String url) {
		return patch(UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP DELETE builder with the given url.
	 * 
	 * @param url the URL
	 * @return the created builder
	 */
	public static HeadersBuilder<?> delete(URI url) {
		return method(HttpMethod.DELETE, url);
	}

	public static HeadersBuilder<?> delete(String url) {
		return delete(UriUtils.toUri(url));
	}

	/**
	 * Creates an HTTP OPTIONS builder with the given url.
	 * 
	 * @param url the URL
	 * @return the created builder
	 */
	public static HeadersBuilder<?> options(URI url) {
		return method(HttpMethod.OPTIONS, url);
	}

	public static HeadersBuilder<?> options(String url) {
		return options(UriUtils.toUri(url));
	}

	/**
	 * Defines a builder that adds headers to the request entity.
	 * 
	 * @param <B> the builder subclass
	 */
	public interface HeadersBuilder<B extends HeadersBuilder<B>> {

		B header(String headerName, String... headerValues);

		B headers(@Nullable HttpHeaders headers);

		B accept(MediaType... acceptableMediaTypes);

		B acceptCharset(Charset... acceptableCharsets);

		B ifModifiedSince(long ifModifiedSince);

		B ifNoneMatch(String... ifNoneMatches);

		<T> HttpRequestEntity<T> build();
	}

	/**
	 * Defines a builder that adds a body to the request entity.
	 */
	public interface BodyBuilder<B extends BodyBuilder<B>> extends HeadersBuilder<B> {

		B contentLength(long contentLength);

		B contentType(MediaType contentType);

		<T> HttpRequestEntity<T> body(T body);

		<T> HttpRequestEntity<T> body(T body, TypeDescriptor typeDescriptor);
	}

	private static class DefaultBodyBuilder implements BodyBuilder<DefaultBodyBuilder> {

		private final String method;

		private final URI url;

		private final HttpHeaders headers = new HttpHeaders();

		public DefaultBodyBuilder(HttpMethod method, URI url) {
			this(method.name(), url);
		}

		public DefaultBodyBuilder(String method, URI url) {
			this.method = method;
			this.url = url;
		}

		public DefaultBodyBuilder header(String headerName, String... headerValues) {
			for (String headerValue : headerValues) {
				this.headers.add(headerName, headerValue);
			}
			return this;
		}

		public DefaultBodyBuilder headers(@Nullable HttpHeaders headers) {
			if (headers != null) {
				this.headers.putAll(headers);
			}
			return this;
		}

		public DefaultBodyBuilder accept(MediaType... acceptableMediaTypes) {
			this.headers.setAccept(Arrays.asList(acceptableMediaTypes));
			return this;
		}

		public DefaultBodyBuilder acceptCharset(Charset... acceptableCharsets) {
			this.headers.setAcceptCharset(Arrays.asList(acceptableCharsets));
			return this;
		}

		public DefaultBodyBuilder contentLength(long contentLength) {
			this.headers.setContentLength(contentLength);
			return this;
		}

		public DefaultBodyBuilder contentType(MediaType contentType) {
			this.headers.setContentType(contentType);
			return this;
		}

		public DefaultBodyBuilder ifNoneMatch(String... ifNoneMatches) {
			this.headers.setIfNoneMatch(Arrays.asList(ifNoneMatches));
			return this;
		}

		public DefaultBodyBuilder ifModifiedSince(long ifModifiedSince) {
			this.headers.setIfModifiedSince(ifModifiedSince);
			return this;
		}

		@Override
		public <T> HttpRequestEntity<T> build() {
			return new HttpRequestEntity<>(this.headers, this.method, this.url);
		}

		public <T> HttpRequestEntity<T> body(T body) {
			return new HttpRequestEntity<T>(body, this.headers, this.method, this.url);
		}

		public <T> HttpRequestEntity<T> body(T body, TypeDescriptor typeDescriptor) {
			return new HttpRequestEntity<T>(body, this.headers, this.method, this.url, typeDescriptor);
		}
	}
}

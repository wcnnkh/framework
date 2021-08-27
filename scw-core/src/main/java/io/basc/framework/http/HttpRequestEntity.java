package io.basc.framework.http;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.utils.ObjectUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.util.MultiValueMap;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;

public class HttpRequestEntity<T> extends HttpEntity<T> implements HttpRequest {
	private static final long serialVersionUID = 1L;

	@Nullable
	private final HttpMethod method;

	private final URI url;

	@Nullable
	private final TypeDescriptor typeDescriptor;

	/**
	 * Constructor with method and URL but without body nor headers.
	 * 
	 * @param method
	 *            the method
	 * @param url
	 *            the URL
	 */
	public HttpRequestEntity(HttpMethod method, URI url) {
		this(null, null, method, url, null);
	}

	/**
	 * Constructor with method, URL and body but without headers.
	 * 
	 * @param body
	 *            the body
	 * @param method
	 *            the method
	 * @param url
	 *            the URL
	 */
	public HttpRequestEntity(@Nullable T body, HttpMethod method, URI url) {
		this(body, null, method, url, null);
	}

	/**
	 * Constructor with method, URL, body and type but without headers.
	 * 
	 * @param body
	 *            the body
	 * @param method
	 *            the method
	 * @param url
	 *            the URL
	 * @param type
	 *            the type used for generic type resolution
	 */
	public HttpRequestEntity(@Nullable T body, HttpMethod method, URI url, TypeDescriptor typeDescriptor)
	{
		this(body, null, method, url, typeDescriptor);
	}

	/**
	 * Constructor with method, URL and headers but without body.
	 * 
	 * @param headers
	 *            the headers
	 * @param method
	 *            the method
	 * @param url
	 *            the URL
	 */
	public HttpRequestEntity(MultiValueMap<String, String> headers,
			HttpMethod method, URI url) {
		this(null, headers, method, url, null);
	}

	/**
	 * Constructor with method, URL, headers and body.
	 * 
	 * @param body
	 *            the body
	 * @param headers
	 *            the headers
	 * @param method
	 *            the method
	 * @param url
	 *            the URL
	 */
	public HttpRequestEntity(@Nullable T body,
			@Nullable MultiValueMap<String, String> headers,
			@Nullable HttpMethod method, URI url) {

		this(body, headers, method, url, null);
	}

	/**
	 * Constructor with method, URL, headers, body and type.
	 * 
	 * @param body
	 *            the body
	 * @param headers
	 *            the headers
	 * @param method
	 *            the method
	 * @param url
	 *            the URL
	 * @param type
	 *            the type used for generic type resolution
	 */
	public HttpRequestEntity(@Nullable T body,
			@Nullable MultiValueMap<String, String> headers,
			@Nullable HttpMethod method, URI url, @Nullable TypeDescriptor typeDescriptor) {

		super(body, headers);
		this.method = method;
		this.url = url;
		this.typeDescriptor = typeDescriptor;
	}

	/**
	 * Return the HTTP method of the request.
	 * 
	 * @return the HTTP method as an {@code HttpMethod} enum value
	 */
	@Nullable
	public HttpMethod getMethod() {
		return this.method;
	}
	
	/**
	 * Return the URI of the request.
	 * 
	 * @return the URL as a {@code URI}
	 */
	@Override
	public final URI getURI() {
		return this.url;
	}

	/**
	 * Return the type of the request's body.
	 * 
	 * @return the request's body type, or {@code null} if not known
	 */
	@Nullable
	public TypeDescriptor getTypeDescriptor() {
		if (this.typeDescriptor== null) {
			T body = getBody();
			if (body != null) {
				return TypeDescriptor.forObject(body);
			}
		}
		return this.typeDescriptor;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other)) {
			return false;
		}
		
		if(other instanceof HttpRequestEntity){
			HttpRequestEntity<?> otherEntity = (HttpRequestEntity<?>) other;
			return (ObjectUtils
					.nullSafeEquals(getMethod(), otherEntity.getMethod()) && ObjectUtils
					.nullSafeEquals(getURI(), otherEntity.getURI()));
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = super.hashCode();
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.method);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.url);
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
	 * @param method
	 *            the HTTP method (GET, POST, etc)
	 * @param url
	 *            the URL
	 * @return the created builder
	 */
	public static BodyBuilder method(HttpMethod method, URI url) {
		return new DefaultBodyBuilder(method, url);
	}
	
	public static BodyBuilder method(HttpMethod method, String url) {
		return method(method, UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP GET builder with the given url.
	 * 
	 * @param url
	 *            the URL
	 * @return the created builder
	 */
	public static HeadersBuilder<?> get(URI url) {
		return method(HttpMethod.GET, url);
	}
	
	public static HeadersBuilder<?> get(String url){
		return get(UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP HEAD builder with the given url.
	 * 
	 * @param url
	 *            the URL
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
	 * @param url
	 *            the URL
	 * @return the created builder
	 */
	public static BodyBuilder post(URI url) {
		return method(HttpMethod.POST, url);
	}
	
	public static BodyBuilder post(String url) {
		return post(UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP PUT builder with the given url.
	 * 
	 * @param url
	 *            the URL
	 * @return the created builder
	 */
	public static BodyBuilder put(URI url) {
		return method(HttpMethod.PUT, url);
	}
	
	public static BodyBuilder put(String url) {
		return put(UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP PATCH builder with the given url.
	 * 
	 * @param url
	 *            the URL
	 * @return the created builder
	 */
	public static BodyBuilder patch(URI url) {
		return method(HttpMethod.PATCH, url);
	}
	
	public static BodyBuilder patch(String url) {
		return patch(UriUtils.toUri(url));
	}

	/**
	 * Create an HTTP DELETE builder with the given url.
	 * 
	 * @param url
	 *            the URL
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
	 * @param url
	 *            the URL
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
	 * @param <B>
	 *            the builder subclass
	 */
	public interface HeadersBuilder<B extends HeadersBuilder<B>> {

		/**
		 * Add the given, single header value under the given name.
		 * 
		 * @param headerName
		 *            the header name
		 * @param headerValues
		 *            the header value(s)
		 * @return this builder
		 * @see HttpHeaders#add(String, String)
		 */
		B header(String headerName, String... headerValues);

		/**
		 * Copy the given headers into the entity's headers map.
		 * 
		 * @param headers
		 *            the existing HttpHeaders to copy from
		 * @return this builder
		 * @see HttpHeaders#add(String, String)
		 */
		B headers(@Nullable HttpHeaders headers);

		/**
		 * Set the list of acceptable {@linkplain MediaType media types}, as
		 * specified by the {@code Accept} header.
		 * 
		 * @param acceptableMediaTypes
		 *            the acceptable media types
		 */
		B accept(MediaType... acceptableMediaTypes);

		/**
		 * Set the list of acceptable {@linkplain Charset charsets}, as
		 * specified by the {@code Accept-Charset} header.
		 * 
		 * @param acceptableCharsets
		 *            the acceptable charsets
		 */
		B acceptCharset(Charset... acceptableCharsets);

		/**
		 * Set the value of the {@code If-Modified-Since} header.
		 * <p>
		 * The date should be specified as the number of milliseconds since
		 * January 1, 1970 GMT.
		 * 
		 * @param ifModifiedSince
		 *            the new value of the header
		 */
		B ifModifiedSince(long ifModifiedSince);

		/**
		 * Set the values of the {@code If-None-Match} header.
		 * 
		 * @param ifNoneMatches
		 *            the new value of the header
		 */
		B ifNoneMatch(String... ifNoneMatches);

		/**
		 * Builds the request entity with no body.
		 * 
		 * @return the request entity
		 * @see BodyBuilder#body(Object)
		 */
		HttpRequestEntity<Void> build();
	}
	
	/**
	 * Defines a builder that adds a body to the request entity.
	 */
	public interface BodyBuilder extends HeadersBuilder<BodyBuilder> {

		/**
		 * Set the length of the body in bytes, as specified by the
		 * {@code Content-Length} header.
		 * 
		 * @param contentLength
		 *            the content length
		 * @return this builder
		 * @see HttpHeaders#setContentLength(long)
		 */
		BodyBuilder contentLength(long contentLength);

		/**
		 * Set the {@linkplain MediaType media type} of the body, as specified
		 * by the {@code Content-Type} header.
		 * 
		 * @param contentType
		 *            the content type
		 * @return this builder
		 * @see HttpHeaders#setContentType(MediaType)
		 */
		BodyBuilder contentType(MediaType contentType);

		/**
		 * Set the body of the request entity and build the RequestEntity.
		 * 
		 * @param <T>
		 *            the type of the body
		 * @param body
		 *            the body of the request entity
		 * @return the built request entity
		 */
		<T> HttpRequestEntity<T> body(T body);

		/**
		 * Set the body and type of the request entity and build the
		 * RequestEntity.
		 * 
		 * @param <T>
		 *            the type of the body
		 * @param body
		 *            the body of the request entity
		 * @param type
		 *            the type of the body, useful for generic type resolution
		 * @return the built request entity
		 */
		<T> HttpRequestEntity<T> body(T body, TypeDescriptor typeDescriptor);
	}
	
	private static class DefaultBodyBuilder implements BodyBuilder {

		private final HttpMethod method;

		private final URI url;

		private final HttpHeaders headers = new HttpHeaders();

		public DefaultBodyBuilder(HttpMethod method, URI url) {
			this.method = method;
			this.url = url;
		}
		
		public BodyBuilder header(String headerName, String... headerValues) {
			for (String headerValue : headerValues) {
				this.headers.add(headerName, headerValue);
			}
			return this;
		}

		public BodyBuilder headers(@Nullable HttpHeaders headers) {
			if (headers != null) {
				this.headers.putAll(headers);
			}
			return this;
		}

		public BodyBuilder accept(MediaType... acceptableMediaTypes) {
			this.headers.setAccept(Arrays.asList(acceptableMediaTypes));
			return this;
		}

		public BodyBuilder acceptCharset(Charset... acceptableCharsets) {
			this.headers.setAcceptCharset(Arrays.asList(acceptableCharsets));
			return this;
		}

		public BodyBuilder contentLength(long contentLength) {
			this.headers.setContentLength(contentLength);
			return this;
		}

		public BodyBuilder contentType(MediaType contentType) {
			this.headers.setContentType(contentType);
			return this;
		}
		
		public BodyBuilder ifNoneMatch(String... ifNoneMatches) {
			this.headers.setIfNoneMatch(Arrays.asList(ifNoneMatches));
			return this;
		}
		
		public BodyBuilder ifModifiedSince(long ifModifiedSince) {
			this.headers.setIfModifiedSince(ifModifiedSince);
			return this;
		}

		public HttpRequestEntity<Void> build() {
			return new HttpRequestEntity<Void>(this.headers, this.method, this.url);
		}

		public <T> HttpRequestEntity<T> body(T body) {
			return new HttpRequestEntity<T>(body, this.headers, this.method, this.url);
		}

		public <T> HttpRequestEntity<T> body(T body, TypeDescriptor typeDescriptor) {
			return new HttpRequestEntity<T>(body, this.headers, this.method, this.url, typeDescriptor);
		}
	}
}

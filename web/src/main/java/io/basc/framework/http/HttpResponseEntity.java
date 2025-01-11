/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.basc.framework.http;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.collections.MultiValueMap;

public class HttpResponseEntity<T> extends HttpEntity<T> {
	private static final long serialVersionUID = 1L;
	private final Object status;

	public HttpResponseEntity(HttpStatus status) {
		this(null, null, status);
	}

	public HttpResponseEntity(T body, TypeDescriptor bodyTypeDescriptor, HttpStatus status) {
		this(body, bodyTypeDescriptor, null, status);
	}

	public HttpResponseEntity(MultiValueMap<String, String> headers, HttpStatus status) {
		this(null, null, headers, status);
	}

	public HttpResponseEntity(T body, TypeDescriptor bodyTypeDescriptor, MultiValueMap<String, String> headers,
			HttpStatus status) {
		super(body, bodyTypeDescriptor, headers);
		Assert.notNull(status, "HttpStatus must not be null");
		this.status = status;
	}

	private HttpResponseEntity(T body, TypeDescriptor bodyTypeDescriptor, MultiValueMap<String, String> headers,
			Object status) {
		super(body, bodyTypeDescriptor, headers);
		this.status = status;
	}

	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an HttpStatus enum entry
	 */
	public HttpStatus getStatusCode() {
		if (this.status instanceof HttpStatus) {
			return (HttpStatus) this.status;
		} else {
			return HttpStatus.valueOf((Integer) this.status);
		}
	}

	/**
	 * Return the HTTP status code of the response.
	 * 
	 * @return the HTTP status as an int value
	 */
	public int getStatusCodeValue() {
		if (this.status instanceof HttpStatus) {
			return ((HttpStatus) this.status).value();
		} else {
			return (Integer) this.status;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!super.equals(other)) {
			return false;
		}
		HttpResponseEntity<?> otherEntity = (HttpResponseEntity<?>) other;
		return ObjectUtils.equals(this.status, otherEntity.status);
	}

	@Override
	public int hashCode() {
		return (super.hashCode() * 29 + ObjectUtils.hashCode(this.status));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("<");
		builder.append(this.status.toString());
		if (this.status instanceof HttpStatus) {
			builder.append(' ');
			builder.append(((HttpStatus) this.status).getReasonPhrase());
		}
		builder.append(',');
		T body = getBody();
		HttpHeaders headers = getHeaders();
		if (body != null) {
			builder.append(body);
			if (headers != null) {
				builder.append(',');
			}
		}
		if (headers != null) {
			builder.append(headers);
		}
		builder.append('>');
		return builder.toString();
	}

	// Static builder methods

	/**
	 * Create a builder with the given status.
	 * 
	 * @param status the response status
	 * @return the created builder
	 */
	public static BodyBuilder status(HttpStatus status) {
		Assert.notNull(status, "HttpStatus must not be null");
		return new DefaultBuilder(status);
	}

	/**
	 * Create a builder with the given status.
	 * 
	 * @param status the response status
	 * @return the created builder
	 */
	public static BodyBuilder status(int status) {
		return new DefaultBuilder(status);
	}

	/**
	 * Create a builder with the status set to {@linkplain HttpStatus#OK OK}.
	 * 
	 * @return the created builder
	 */
	public static BodyBuilder ok() {
		return status(HttpStatus.OK);
	}

	/**
	 * A shortcut for creating a {@code ResponseEntity} with the given body and the
	 * status set to {@linkplain HttpStatus#OK OK}.
	 * 
	 * @return the created {@code ResponseEntity}
	 */
	public static <T> HttpResponseEntity<T> ok(T body) {
		BodyBuilder builder = ok();
		return builder.body(body);
	}

	/**
	 * Create a new builder with a {@linkplain HttpStatus#CREATED CREATED} status
	 * and a location header set to the given URI.
	 * 
	 * @param location the location URI
	 * @return the created builder
	 */
	public static BodyBuilder created(URI location) {
		BodyBuilder builder = status(HttpStatus.CREATED);
		return builder.location(location);
	}

	/**
	 * Create a builder with an {@linkplain HttpStatus#ACCEPTED ACCEPTED} status.
	 * 
	 * @return the created builder
	 */
	public static BodyBuilder accepted() {
		return status(HttpStatus.ACCEPTED);
	}

	/**
	 * Create a builder with a {@linkplain HttpStatus#NO_CONTENT NO_CONTENT} status.
	 * 
	 * @return the created builder
	 */
	public static HeadersBuilder<?> noContent() {
		return status(HttpStatus.NO_CONTENT);
	}

	/**
	 * Create a builder with a {@linkplain HttpStatus#BAD_REQUEST BAD_REQUEST}
	 * status.
	 * 
	 * @return the created builder
	 */
	public static BodyBuilder badRequest() {
		return status(HttpStatus.BAD_REQUEST);
	}

	/**
	 * Create a builder with a {@linkplain HttpStatus#NOT_FOUND NOT_FOUND} status.
	 * 
	 * @return the created builder
	 */
	public static HeadersBuilder<?> notFound() {
		return status(HttpStatus.NOT_FOUND);
	}

	/**
	 * Create a builder with an {@linkplain HttpStatus#UNPROCESSABLE_ENTITY
	 * UNPROCESSABLE_ENTITY} status.
	 * 
	 * @return the created builder
	 */
	public static BodyBuilder unprocessableEntity() {
		return status(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	/**
	 * Defines a builder that adds headers to the response entity.
	 * 
	 * @param <B> the builder subclass
	 */
	public interface HeadersBuilder<B extends HeadersBuilder<B>> {

		/**
		 * Add the given, single header value under the given name.
		 * 
		 * @param headerName   the header name
		 * @param headerValues the header value(s)
		 * @return this builder
		 * @see HttpHeaders#add(String, String)
		 */
		B header(String headerName, String... headerValues);

		/**
		 * Copy the given headers into the entity's headers map.
		 * 
		 * @param headers the existing HttpHeaders to copy from
		 * @return this builder
		 * @see HttpHeaders#add(String, String)
		 */
		B headers(HttpHeaders headers);

		/**
		 * Set the set of allowed {@link HttpMethod HTTP methods}, as specified by the
		 * {@code Allow} header.
		 * 
		 * @param allowedMethods the allowed methods
		 * @return this builder
		 * @see HttpHeaders#setAllow(Set)
		 */
		B allow(HttpMethod... allowedMethods);

		/**
		 * Set the entity tag of the body, as specified by the {@code ETag} header.
		 * 
		 * @param etag the new entity tag
		 * @return this builder
		 * @see HttpHeaders#setETag(String)
		 */
		B eTag(String etag);

		/**
		 * Set the time the resource was last changed, as specified by the
		 * {@code Last-Modified} header.
		 * <p>
		 * The date should be specified as the number of milliseconds since January 1,
		 * 1970 GMT.
		 * 
		 * @param lastModified the last modified date
		 * @return this builder
		 * @see HttpHeaders#setLastModified(long)
		 */
		B lastModified(long lastModified);

		/**
		 * Set the location of a resource, as specified by the {@code Location} header.
		 * 
		 * @param location the location
		 * @return this builder
		 * @see HttpHeaders#setLocation(URI)
		 */
		B location(URI location);

		/**
		 * Set the caching directives for the resource, as specified by the HTTP 1.1
		 * {@code Cache-Control} header.
		 * <p>
		 * A {@code CacheControl} instance can be built like
		 * {@code CacheControl.maxAge(3600).cachePublic().noTransform()}.
		 * 
		 * @param cacheControl a builder for cache-related HTTP response headers
		 * @return this builder
		 * @see <a href="https://tools.ietf.org/html/rfc7234#section-5.2">RFC-7234
		 *      Section 5.2</a>
		 */
		B cacheControl(CacheControl cacheControl);

		/**
		 * Configure one or more request header names (e.g. "Accept-Language") to add to
		 * the "Vary" response header to inform clients that the response is subject to
		 * content negotiation and variances based on the value of the given request
		 * headers. The configured request header names are added only if not already
		 * present in the response "Vary" header.
		 * 
		 * @param requestHeaders request header names
		 */
		B varyBy(String... requestHeaders);

		/**
		 * Build the response entity with no body.
		 * 
		 * @return the response entity
		 * @see BodyBuilder#body(Object)
		 */
		<T> HttpResponseEntity<T> build();
	}

	/**
	 * Defines a builder that adds a body to the response entity.
	 */
	public interface BodyBuilder extends HeadersBuilder<BodyBuilder> {

		/**
		 * Set the length of the body in bytes, as specified by the
		 * {@code Content-Length} header.
		 * 
		 * @param contentLength the content length
		 * @return this builder
		 * @see HttpHeaders#setContentLength(long)
		 */
		BodyBuilder contentLength(long contentLength);

		/**
		 * Set the {@linkplain MediaType media type} of the body, as specified by the
		 * {@code Content-Type} header.
		 * 
		 * @param contentType the content type
		 * @return this builder
		 * @see HttpHeaders#setContentType(MediaType)
		 */
		BodyBuilder contentType(MediaType contentType);

		/**
		 * Set the body of the response entity and returns it.
		 * 
		 * @param <T>  the type of the body
		 * @param body the body of the response entity
		 * @return the built response entity
		 */
		default <T> HttpResponseEntity<T> body(T body) {
			return body(body, null);
		}

		<T> HttpResponseEntity<T> body(T body, @Nullable TypeDescriptor bodyTypeDescriptor);
	}

	private static class DefaultBuilder implements BodyBuilder {

		private final Object statusCode;

		private final HttpHeaders headers = new HttpHeaders();

		public DefaultBuilder(Object statusCode) {
			this.statusCode = statusCode;
		}

		public BodyBuilder header(String headerName, String... headerValues) {
			for (String headerValue : headerValues) {
				this.headers.add(headerName, headerValue);
			}
			return this;
		}

		public BodyBuilder headers(HttpHeaders headers) {
			if (headers != null) {
				this.headers.putAll(headers);
			}
			return this;
		}

		public BodyBuilder allow(HttpMethod... allowedMethods) {
			this.headers.setAllow(new LinkedHashSet<HttpMethod>(Arrays.asList(allowedMethods)));
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

		public BodyBuilder eTag(String etag) {
			if (etag != null) {
				if (!etag.startsWith("\"") && !etag.startsWith("W/\"")) {
					etag = "\"" + etag;
				}
				if (!etag.endsWith("\"")) {
					etag = etag + "\"";
				}
			}
			this.headers.setETag(etag);
			return this;
		}

		public BodyBuilder lastModified(long date) {
			this.headers.setLastModified(date);
			return this;
		}

		public BodyBuilder location(URI location) {
			this.headers.setLocation(location);
			return this;
		}

		public BodyBuilder cacheControl(CacheControl cacheControl) {
			String ccValue = cacheControl.getHeaderValue();
			if (ccValue != null) {
				this.headers.setCacheControl(ccValue);
			}
			return this;
		}

		public BodyBuilder varyBy(String... requestHeaders) {
			this.headers.setVary(Arrays.asList(requestHeaders));
			return this;
		}

		public <T> HttpResponseEntity<T> build() {
			return body(null);
		}

		public <T> HttpResponseEntity<T> body(T body, TypeDescriptor typeDescriptor) {
			return new HttpResponseEntity<T>(body, typeDescriptor, this.headers, this.statusCode);
		}
	}

}

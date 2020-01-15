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

package scw.http.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import scw.core.Assert;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.InvalidMediaTypeException;
import scw.http.MediaType;
import scw.util.LinkedCaseInsensitiveMap;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

/**
 * {@link ServerHttpRequest} implementation that is based on a
 * {@link HttpServletRequest}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.0
 */
public class ServletServerHttpRequest implements ServerHttpRequest {

	protected static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

	protected static final String FORM_CHARSET = "UTF-8";

	private final HttpServletRequest servletRequest;

	private URI uri;

	private HttpHeaders headers;

	private ServerHttpAsyncRequestControl asyncRequestControl;
	private MultiValueMap<String, String> parameterMap;

	/**
	 * Construct a new instance of the ServletServerHttpRequest based on the
	 * given {@link HttpServletRequest}.
	 * 
	 * @param servletRequest
	 *            the servlet request
	 */
	public ServletServerHttpRequest(HttpServletRequest servletRequest) {
		Assert.notNull(servletRequest, "HttpServletRequest must not be null");
		this.servletRequest = servletRequest;
		this.parameterMap = new LinkedMultiValueMap<String, String>();
		Map<String, String[]> paramMap = servletRequest.getParameterMap();
		for (Entry<String, String[]> entry : paramMap.entrySet()) {
			parameterMap.put(entry.getKey(), Arrays.asList(entry.getValue()));
		}
	}

	/**
	 * Returns the {@code HttpServletRequest} this object is based on.
	 */
	public HttpServletRequest getServletRequest() {
		return this.servletRequest;
	}

	public HttpMethod getMethod() {
		return HttpMethod.resolve(this.servletRequest.getMethod());
	}

	public URI getURI() {
		if (this.uri == null) {
			String urlString = null;
			boolean hasQuery = false;
			try {
				StringBuffer url = this.servletRequest.getRequestURL();
				String query = this.servletRequest.getQueryString();
				hasQuery = StringUtils.hasText(query);
				if (hasQuery) {
					url.append('?').append(query);
				}
				urlString = url.toString();
				this.uri = new URI(urlString);
			} catch (URISyntaxException ex) {
				if (!hasQuery) {
					throw new IllegalStateException("Could not resolve HttpServletRequest as URI: " + urlString, ex);
				}
				// Maybe a malformed query string... try plain request URL
				try {
					urlString = this.servletRequest.getRequestURL().toString();
					this.uri = new URI(urlString);
				} catch (URISyntaxException ex2) {
					throw new IllegalStateException("Could not resolve HttpServletRequest as URI: " + urlString, ex2);
				}
			}
		}
		return this.uri;
	}

	public HttpHeaders getHeaders() {
		if (this.headers == null) {
			this.headers = new HttpHeaders();

			for (Enumeration<?> names = this.servletRequest.getHeaderNames(); names.hasMoreElements();) {
				String headerName = (String) names.nextElement();
				for (Enumeration<?> headerValues = this.servletRequest.getHeaders(headerName); headerValues
						.hasMoreElements();) {
					String headerValue = (String) headerValues.nextElement();
					this.headers.add(headerName, headerValue);
				}
			}

			// HttpServletRequest exposes some headers as properties:
			// we should include those if not already present
			try {
				MediaType contentType = this.headers.getContentType();
				if (contentType == null) {
					String requestContentType = this.servletRequest.getContentType();
					if (StringUtils.hasLength(requestContentType)) {
						contentType = MediaType.parseMediaType(requestContentType);
						this.headers.setContentType(contentType);
					}
				}
				if (contentType != null && contentType.getCharset() == null) {
					String requestEncoding = this.servletRequest.getCharacterEncoding();
					if (StringUtils.hasLength(requestEncoding)) {
						Charset charSet = Charset.forName(requestEncoding);
						Map<String, String> params = new LinkedCaseInsensitiveMap<String>();
						params.putAll(contentType.getParameters());
						params.put("charset", charSet.toString());
						MediaType mediaType = new MediaType(contentType.getType(), contentType.getSubtype(), params);
						this.headers.setContentType(mediaType);
					}
				}
			} catch (InvalidMediaTypeException ex) {
				// Ignore: simply not exposing an invalid content type in
				// HttpHeaders...
			}

			if (this.headers.getContentLength() < 0) {
				int requestContentLength = this.servletRequest.getContentLength();
				if (requestContentLength != -1) {
					this.headers.setContentLength(requestContentLength);
				}
			}
		}

		return this.headers;
	}

	public Principal getPrincipal() {
		return this.servletRequest.getUserPrincipal();
	}

	public InetSocketAddress getLocalAddress() {
		return new InetSocketAddress(this.servletRequest.getLocalName(), this.servletRequest.getLocalPort());
	}

	public InetSocketAddress getRemoteAddress() {
		return new InetSocketAddress(this.servletRequest.getRemoteHost(), this.servletRequest.getRemotePort());
	}

	public InputStream getBody() throws IOException {
		if (isFormPost(this.servletRequest)) {
			return getBodyFromServletRequestParameters(this.servletRequest);
		} else {
			return this.servletRequest.getInputStream();
		}
	}

	public ServerHttpAsyncRequestControl getAsyncRequestControl(ServerHttpResponse response) {
		if (this.asyncRequestControl == null) {
			if (!ServletServerHttpResponse.class.isInstance(response)) {
				throw new IllegalArgumentException(
						"Response must be a ServletServerHttpResponse: " + response.getClass());
			}
			ServletServerHttpResponse servletServerResponse = (ServletServerHttpResponse) response;
			this.asyncRequestControl = new ServletServerHttpAsyncRequestControl(this, servletServerResponse);
		}
		return this.asyncRequestControl;
	}

	private static boolean isFormPost(HttpServletRequest request) {
		String contentType = request.getContentType();
		return (contentType != null && contentType.contains(FORM_CONTENT_TYPE)
				&& HttpMethod.POST.matches(request.getMethod()));
	}

	/**
	 * Use {@link javax.servlet.ServletRequest#getParameterMap()} to reconstruct
	 * the body of a form 'POST' providing a predictable outcome as opposed to
	 * reading from the body, which can fail if any other code has used the
	 * ServletRequest to access a parameter, thus causing the input stream to be
	 * "consumed".
	 */
	private static InputStream getBodyFromServletRequestParameters(HttpServletRequest request) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
		Writer writer = new OutputStreamWriter(bos, FORM_CHARSET);

		Map<String, String[]> form = request.getParameterMap();
		for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();) {
			String name = nameIterator.next();
			List<String> values = Arrays.asList(form.get(name));
			for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext();) {
				String value = valueIterator.next();
				writer.write(URLEncoder.encode(name, FORM_CHARSET));
				if (value != null) {
					writer.write('=');
					writer.write(URLEncoder.encode(value, FORM_CHARSET));
					if (valueIterator.hasNext()) {
						writer.write('&');
					}
				}
			}
			if (nameIterator.hasNext()) {
				writer.append('&');
			}
		}
		writer.flush();

		return new ByteArrayInputStream(bos.toByteArray());
	}

	public boolean isSupportAsyncRequestControl() {
		return true;
	}

	public MultiValueMap<String, String> getParameterMap() {
		return CollectionUtils.unmodifiableMultiValueMap(parameterMap);
	}
}
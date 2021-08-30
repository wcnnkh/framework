package io.basc.framework.web.servlet.http;

import io.basc.framework.http.HttpCookie;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.InvalidMediaTypeException;
import io.basc.framework.http.MediaType;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.security.session.Session;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Decorator;
import io.basc.framework.util.LinkedCaseInsensitiveMap;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.web.ServerHttpAsyncControl;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.servlet.ServletUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ServletServerHttpRequest implements ServerHttpRequest, Decorator {
	private HttpHeaders headers;
	private HttpServletRequest httpServletRequest;
	private HttpServletAsyncControl asyncControl;
	private MultiValueMap<String, String> parameterMap;
	private MultiValueMap<String, String> restfulParameterMap;

	public ServletServerHttpRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public String getPath() {
		return httpServletRequest.getServletPath();
	}

	public <T> T getDelegate(Class<T> targetType) {
		return XUtils.getDelegate(httpServletRequest, targetType);
	}

	public HttpCookie[] getCookies() {
		javax.servlet.http.Cookie[] cookies = httpServletRequest.getCookies();
		if (cookies == null) {
			return new HttpCookie[0];
		}

		HttpCookie[] values = new HttpCookie[cookies.length];
		for (int i = 0; i < cookies.length; i++) {
			values[i] = ServletUtils.wrapper(cookies[i]).readyOnly();
		}
		return values;
	}

	public Session getSession() {
		HttpSession session = httpServletRequest.getSession();
		return session == null ? null : new ServletHttpSession(session);
	}

	public Session getSession(boolean create) {
		HttpSession httpSession = httpServletRequest.getSession(create);
		return httpSession == null ? null : new ServletHttpSession(httpSession);
	}

	public Principal getPrincipal() {
		return httpServletRequest.getUserPrincipal();
	}

	private InetSocketAddress localAddress;

	public InetSocketAddress getLocalAddress() {
		if (localAddress == null) {
			localAddress = new InetSocketAddress(this.httpServletRequest.getLocalName(),
					this.httpServletRequest.getLocalPort());
		}
		return localAddress;
	}

	private InetSocketAddress remoteAddress;

	public InetSocketAddress getRemoteAddress() {
		if (remoteAddress == null) {
			remoteAddress = new InetSocketAddress(this.httpServletRequest.getRemoteHost(),
					this.httpServletRequest.getRemotePort());
		}
		return remoteAddress;
	}

	public HttpHeaders getHeaders() {
		if (this.headers == null) {
			this.headers = new HttpHeaders();
			for (Enumeration<?> names = httpServletRequest.getHeaderNames(); names.hasMoreElements();) {
				String headerName = (String) names.nextElement();
				for (Enumeration<?> headerValues = httpServletRequest.getHeaders(headerName); headerValues
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
					String requestContentType = httpServletRequest.getContentType();
					if (StringUtils.hasLength(requestContentType)) {
						this.headers.set(HttpHeaders.CONTENT_TYPE, requestContentType);
					}
				}
				if (contentType != null && contentType.getCharset() == null) {
					String requestEncoding = getCharacterEncoding();
					if (StringUtils.hasLength(requestEncoding)) {
						Charset charSet = Charset.forName(requestEncoding);
						Map<String, String> params = new LinkedCaseInsensitiveMap<String>();
						params.putAll(contentType.getParameters());
						params.put("charset", charSet.name());
						MediaType mediaType = new MediaType(contentType.getType(), contentType.getSubtype(), params);
						this.headers.set(HttpHeaders.CONTENT_TYPE, mediaType.toString());
					}
				}
			} catch (InvalidMediaTypeException ex) {
				// Ignore: simply not exposing an invalid content type in
				// HttpHeaders...
			}

			if (this.headers.getContentLength() < 0) {
				int requestContentLength = httpServletRequest.getContentLength();
				if (requestContentLength != -1) {
					this.headers.setContentLength(requestContentLength);
				}
			}
		}
		return this.headers;
	}

	public HttpMethod getMethod() {
		return HttpMethod.resolve(getRawMethod());
	}

	public URI getURI() {
		return UriUtils.toUri(httpServletRequest.getRequestURI());
	}

	public BufferedReader getReader() throws IOException {
		return httpServletRequest.getReader();
	}

	public InputStream getInputStream() throws IOException {
		return httpServletRequest.getInputStream();
	}

	private void initParameterMap() {
		if (parameterMap != null) {
			return;
		}

		Map<String, String[]> map = httpServletRequest.getParameterMap();
		if (map.isEmpty()) {
			this.parameterMap = CollectionUtils.emptyMultiValueMap();
			return;
		}

		Map<String, List<String>> valueMap = new HashMap<String, List<String>>();
		for (Entry<String, String[]> entry : map.entrySet()) {
			String[] values = entry.getValue();
			if (values == null || values.length == 0) {
				continue;
			}

			valueMap.put(entry.getKey(), Arrays.asList(values));
		}

		this.parameterMap = CollectionUtils.toMultiValueMap(Collections.unmodifiableMap(valueMap));
	}

	public MultiValueMap<String, String> getParameterMap() {
		initParameterMap();
		return parameterMap;
	}

	@Override
	public String getCharacterEncoding() {
		String charsetName = ServerHttpRequest.super.getCharacterEncoding();
		return charsetName == null ? httpServletRequest.getCharacterEncoding() : charsetName;
	}

	public String getRawMethod() {
		return httpServletRequest.getMethod();
	}

	public String getContextPath() {
		return httpServletRequest.getContextPath();
	}

	public void setAttribute(String name, Object o) {
		httpServletRequest.setAttribute(name, o);
	}

	public void removeAttribute(String name) {
		httpServletRequest.removeAttribute(name);
	}

	public Object getAttribute(String name) {
		return httpServletRequest.getAttribute(name);
	}

	public Enumeration<String> getAttributeNames() {
		return httpServletRequest.getAttributeNames();
	}

	@Override
	public long getContentLength() {
		return httpServletRequest.getContentLength();
	}

	public boolean isSupportAsyncControl() {
		return httpServletRequest.isAsyncSupported();
	}

	public ServerHttpAsyncControl getAsyncControl(ServerHttpResponse response) {
		if (asyncControl == null) {
			if (response instanceof ServletServerHttpResponse) {
				this.asyncControl = new HttpServletAsyncControl(httpServletRequest,
						((ServletServerHttpResponse) response).getHttpServletResponse());
			} else {
				throw new IllegalArgumentException(
						"Response must be a ServletServerHttpResponse: " + response.getClass());
			}
		}
		return this.asyncControl;
	}

	public String getIp() {
		String ip = getHeaders().getIp();
		return ip == null ? httpServletRequest.getRemoteHost() : ip;
	}

	public MultiValueMap<String, String> getRestfulParameterMap() {
		if (restfulParameterMap == null) {
			return CollectionUtils.emptyMultiValueMap();
		}

		return restfulParameterMap;
	}

	public void setRestfulParameterMap(MultiValueMap<String, String> restfulParameterMap) {
		this.restfulParameterMap = CollectionUtils.unmodifiableMultiValueMap(restfulParameterMap);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(httpServletRequest.getMethod());
		sb.append(" " + httpServletRequest.getServletPath());
		sb.append(" " + httpServletRequest.getProtocol());

		String contentType = httpServletRequest.getContentType();
		if (StringUtils.isNotEmpty(contentType)) {
			sb.append(" " + contentType);
		}

		MultiValueMap<String, String> parameters = getParameterMap();
		if (!CollectionUtils.isEmpty(parameters)) {
			sb.append(" parameters->").append(parameters);
		}

		if (!CollectionUtils.isEmpty(restfulParameterMap)) {
			sb.append(" restful->").append(restfulParameterMap);
		}
		return sb.toString();
	}
}

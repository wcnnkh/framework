package io.basc.framework.web.servlet.http;

import java.net.HttpCookie;
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

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.InvalidMediaTypeException;
import io.basc.framework.http.MediaType;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.util.Decorator;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collect.CollectionUtils;
import io.basc.framework.util.collect.LinkedCaseInsensitiveMap;
import io.basc.framework.util.collect.MultiValueMap;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.Session;
import io.basc.framework.web.servlet.ServletServerRequest;

public class ServletServerHttpRequest extends ServletServerRequest<HttpServletRequest>
		implements ServerHttpRequest, Decorator {
	private HttpHeaders headers;
	private MultiValueMap<String, String> parameterMap;
	private MultiValueMap<String, String> restfulParameterMap;

	public ServletServerHttpRequest(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

	public String getPath() {
		return wrappedTarget.getServletPath();
	}

	public HttpCookie[] getCookies() {
		javax.servlet.http.Cookie[] cookies = wrappedTarget.getCookies();
		if (cookies == null) {
			return new HttpCookie[0];
		}

		HttpCookie[] values = new HttpCookie[cookies.length];
		for (int i = 0; i < cookies.length; i++) {
			values[i] = ServletCookieCodec.INSTANCE.encode(cookies[i]);
		}
		return values;
	}

	public Session getSession() {
		HttpSession session = wrappedTarget.getSession();
		return session == null ? null : new ServletHttpSession(session);
	}

	public Session getSession(boolean create) {
		HttpSession httpSession = wrappedTarget.getSession(create);
		return httpSession == null ? null : new ServletHttpSession(httpSession);
	}

	public Principal getPrincipal() {
		return wrappedTarget.getUserPrincipal();
	}

	public HttpHeaders getHeaders() {
		if (this.headers == null) {
			this.headers = new HttpHeaders();
			for (Enumeration<?> names = wrappedTarget.getHeaderNames(); names.hasMoreElements();) {
				String headerName = (String) names.nextElement();
				for (Enumeration<?> headerValues = wrappedTarget.getHeaders(headerName); headerValues
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
					String requestContentType = wrappedTarget.getContentType();
					if (StringUtils.isNotEmpty(requestContentType)) {
						this.headers.set(HttpHeaders.CONTENT_TYPE, requestContentType);
					}
				}
				if (contentType != null && contentType.getCharset() == null) {
					String requestEncoding = getCharacterEncoding();
					if (StringUtils.isNotEmpty(requestEncoding)) {
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
				int requestContentLength = wrappedTarget.getContentLength();
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
		return UriUtils.toUri(wrappedTarget.getRequestURI());
	}

	private void initParameterMap() {
		if (parameterMap != null) {
			return;
		}

		Map<String, String[]> map = wrappedTarget.getParameterMap();
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
		return charsetName == null ? wrappedTarget.getCharacterEncoding() : charsetName;
	}

	public String getRawMethod() {
		return wrappedTarget.getMethod();
	}

	public String getContextPath() {
		return wrappedTarget.getContextPath();
	}

	public String getIp() {
		String ip = getHeaders().getIp();
		return ip == null ? wrappedTarget.getRemoteHost() : ip;
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
		sb.append(wrappedTarget.getMethod());
		sb.append(" " + wrappedTarget.getServletPath());
		sb.append(" " + wrappedTarget.getProtocol());

		String contentType = wrappedTarget.getContentType();
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

package io.basc.framework.servlet.http;

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
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.net.InvalidMediaTypeException;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.servlet.ServletServerRequestWrepper;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.collections.LinkedCaseInsensitiveMap;
import io.basc.framework.util.collections.MultiValueMap;

public class ServletServerHttpRequest<W extends HttpServletRequest> extends ServletServerRequestWrepper<W>
		implements ServerHttpRequest {
	private HttpHeaders headers;
	private MultiValueMap<String, String> parameterMap;

	public ServletServerHttpRequest(W httpServletRequest) {
		super(httpServletRequest);
	}

	public String getPath() {
		return source.getServletPath();
	}

	public Elements<HttpCookie> getCookies() {
		javax.servlet.http.Cookie[] cookies = source.getCookies();
		if (cookies == null) {
			return Elements.empty();
		}
		return Elements.forArray(cookies).map((e) -> ServletCookieCodec.INSTANCE.encode(e));
	}

	public io.basc.framework.http.HttpSession getSession() {
		HttpSession session = source.getSession();
		return session == null ? null : new ServletHttpSession(session);
	}

	public io.basc.framework.http.HttpSession getSession(boolean create) {
		HttpSession httpSession = source.getSession(create);
		return httpSession == null ? null : new ServletHttpSession(httpSession);
	}

	public Principal getPrincipal() {
		return source.getUserPrincipal();
	}

	public HttpHeaders getHeaders() {
		if (this.headers == null) {
			this.headers = new HttpHeaders();
			for (Enumeration<?> names = source.getHeaderNames(); names.hasMoreElements();) {
				String headerName = (String) names.nextElement();
				for (Enumeration<?> headerValues = source.getHeaders(headerName); headerValues.hasMoreElements();) {
					String headerValue = (String) headerValues.nextElement();
					this.headers.add(headerName, headerValue);
				}
			}

			// HttpServletRequest exposes some headers as properties:
			// we should include those if not already present
			try {
				MediaType contentType = this.headers.getContentType();
				if (contentType == null) {
					String requestContentType = source.getContentType();
					if (StringUtils.isNotEmpty(requestContentType)) {
						this.headers.set(HttpHeaders.CONTENT_TYPE, requestContentType);
					}
				}
				if (contentType != null && contentType.getCharset() == null) {
					String requestEncoding = getCharsetName();
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
				int requestContentLength = source.getContentLength();
				if (requestContentLength != -1) {
					this.headers.setContentLength(requestContentLength);
				}
			}
		}
		return this.headers;
	}

	public URI getURI() {
		return UriUtils.toUri(source.getRequestURI());
	}

	private void initParameterMap() {
		if (parameterMap != null) {
			return;
		}

		Map<String, String[]> map = source.getParameterMap();
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
	public String getCharsetName() {
		String charsetName = super.getCharsetName();
		return charsetName == null ? source.getCharacterEncoding() : charsetName;
	}

	public String getRawMethod() {
		return source.getMethod();
	}

	public String getContextPath() {
		return source.getContextPath();
	}

	public String getIp() {
		String ip = getHeaders().getIp();
		return ip == null ? source.getRemoteHost() : ip;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(source.getMethod());
		sb.append(" " + source.getServletPath());
		sb.append(" " + source.getProtocol());

		String contentType = source.getContentType();
		if (StringUtils.isNotEmpty(contentType)) {
			sb.append(" " + contentType);
		}

		MultiValueMap<String, String> parameters = getParameterMap();
		if (!CollectionUtils.isEmpty(parameters)) {
			sb.append(" parameters->").append(parameters);
		}
		return sb.toString();
	}
}

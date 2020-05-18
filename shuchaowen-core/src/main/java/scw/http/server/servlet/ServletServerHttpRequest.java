package scw.http.server.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import scw.core.utils.StringUtils;
import scw.http.HttpCookie;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.HttpUtils;
import scw.http.InvalidMediaTypeException;
import scw.http.MediaType;
import scw.http.server.ServerHttpAsyncControl;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.net.NetworkUtils;
import scw.net.message.AbstractInputMessage;
import scw.security.session.Session;
import scw.util.LinkedCaseInsensitiveMap;

public class ServletServerHttpRequest extends AbstractInputMessage implements ServerHttpRequest {
	private HttpHeaders headers;
	private HttpServletRequest httpServletRequest;
	private HttpServletAsyncControl asyncControl;

	public ServletServerHttpRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public String getPath() {
		return httpServletRequest.getServletPath();
	}

	public HttpCookie[] getCookies() {
		javax.servlet.http.Cookie[] cookies = httpServletRequest.getCookies();
		if (cookies == null) {
			return new HttpCookie[0];
		}

		HttpCookie[] values = new HttpCookie[cookies.length];
		for (int i = 0; i < cookies.length; i++) {
			values[i] = new ServletHttpCookie(cookies[i]);
		}
		return values;
	}

	public Session getSession() {
		HttpSession session = httpServletRequest.getSession();
		return session == null ? null : new ServletHttpSession(session);
	}

	public Session getSession(boolean create) {
		HttpSession httpSession = httpServletRequest.getSession(create);
		return new ServletHttpSession(httpSession);
	}

	public Principal getPrincipal() {
		return httpServletRequest.getUserPrincipal();
	}

	public InetSocketAddress getLocalAddress() {
		return new InetSocketAddress(this.httpServletRequest.getLocalName(), this.httpServletRequest.getLocalPort());
	}

	public InetSocketAddress getRemoteAddress() {
		return new InetSocketAddress(this.httpServletRequest.getRemoteHost(), this.httpServletRequest.getRemotePort());
	}

	public void setCharacterEncoding(String enc) {
		try {
			httpServletRequest.setCharacterEncoding(enc);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
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
						contentType = MediaType.parseMediaType(requestContentType);
						this.headers.setContentType(contentType);
					}
				}
				if (contentType != null && contentType.getCharset() == null) {
					String requestEncoding = getCharacterEncoding();
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

	public MediaType getContentType() {
		String contentType = httpServletRequest.getContentType();
		return contentType == null? null:MediaType.parseMediaType(contentType);
	}

	public URI getURI() {
		return NetworkUtils.toURI(httpServletRequest.getRequestURI());
	}

	public long getContentLength() {
		return httpServletRequest.getContentLength();
	}

	public String getCharacterEncoding() {
		return httpServletRequest.getCharacterEncoding();
	}

	public BufferedReader getReader() throws IOException {
		return httpServletRequest.getReader();
	}

	public String getRemoteAddr() {
		return httpServletRequest.getRemoteAddr();
	}

	public InputStream getBody() throws IOException {
		return httpServletRequest.getInputStream();
	}

	public String getParameter(String name) {
		return httpServletRequest.getParameter(name);
	}

	public Enumeration<String> getParameterNames() {
		return httpServletRequest.getParameterNames();
	}

	public String[] getParameterValues(String name) {
		return httpServletRequest.getParameterValues(name);
	}

	public Map<String, String[]> getParameterMap() {
		return httpServletRequest.getParameterMap();
	}

	public String getRawMethod() {
		return httpServletRequest.getMethod();
	}

	public String getRemoteHost() {
		return httpServletRequest.getRemoteHost();
	}

	public String getContextPath() {
		return httpServletRequest.getContextPath();
	}

	public String getController() {
		return httpServletRequest.getServletPath();
	}

	@Override
	public String getRawContentType() {
		return httpServletRequest.getContentType();
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
		return HttpUtils.getServerHttpRequestIpGetter().getRequestIp(this);
	}
}

package scw.servlet.mvc.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import scw.core.utils.StringUtils;
import scw.mvc.MVCUtils;
import scw.mvc.http.HttpRequest;
import scw.net.NetworkUtils;
import scw.net.http.Cookie;
import scw.net.http.HttpHeaders;
import scw.net.http.InvalidMediaTypeException;
import scw.net.http.MediaType;
import scw.net.http.Method;
import scw.net.message.AbstractInputMessage;
import scw.security.session.Session;
import scw.servlet.ServletUtils;
import scw.util.LinkedCaseInsensitiveMap;

public class MyHttpServletRequest extends AbstractInputMessage implements
		HttpRequest {
	private HttpHeaders headers;
	private HttpServletRequest httpServletRequest;

	public MyHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}

	public String getRequestPath() {
		return httpServletRequest.getServletPath();
	}

	public Cookie getCookie(String name) {
		if (name == null) {
			return null;
		}

		javax.servlet.http.Cookie cookie = ServletUtils.getCookie(
				httpServletRequest, name);
		if (cookie == null) {
			return null;
		}

		return new HttpServletCookie(cookie);
	}

	public Cookie[] getCookies() {
		javax.servlet.http.Cookie[] cookies = httpServletRequest.getCookies();
		if (cookies == null) {
			return new Cookie[0];
		}

		Cookie[] values = new Cookie[cookies.length];
		for (int i = 0; i < cookies.length; i++) {
			values[i] = new HttpServletCookie(cookies[i]);
		}
		return values;
	}

	public Session getHttpSession() {
		HttpSession session = httpServletRequest.getSession();
		return session == null ? null : new HttpServletSession(session);
	}

	public Session getHttpSession(boolean create) {
		HttpSession httpSession = httpServletRequest.getSession(create);
		return new HttpServletSession(httpSession);
	}

	public String getIP() {
		return MVCUtils.getIP(this);
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

			for (Enumeration<?> names = httpServletRequest.getHeaderNames(); names
					.hasMoreElements();) {
				String headerName = (String) names.nextElement();
				for (Enumeration<?> headerValues = httpServletRequest
						.getHeaders(headerName); headerValues.hasMoreElements();) {
					String headerValue = (String) headerValues.nextElement();
					this.headers.add(headerName, headerValue);
				}
			}

			// HttpServletRequest exposes some headers as properties:
			// we should include those if not already present
			try {
				MediaType contentType = this.headers.getContentType();
				if (contentType == null) {
					String requestContentType = httpServletRequest
							.getContentType();
					if (StringUtils.hasLength(requestContentType)) {
						contentType = MediaType
								.parseMediaType(requestContentType);
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
						MediaType mediaType = new MediaType(
								contentType.getType(),
								contentType.getSubtype(), params);
						this.headers.setContentType(mediaType);
					}
				}
			} catch (InvalidMediaTypeException ex) {
				// Ignore: simply not exposing an invalid content type in
				// HttpHeaders...
			}

			if (this.headers.getContentLength() < 0) {
				int requestContentLength = httpServletRequest
						.getContentLength();
				if (requestContentLength != -1) {
					this.headers.setContentLength(requestContentLength);
				}
			}
		}
		return this.headers;
	}

	public Method getMethod() {
		return Method.resolve(getRawMethod());
	}

	public MediaType getContentType() {
		return MediaType.parseMediaType(httpServletRequest.getContentType());
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

	public String getControllerPath() {
		return httpServletRequest.getServletPath();
	}

	@Override
	public String getRawContentType() {
		return httpServletRequest.getContentType();
	}
}

package io.basc.framework.web.servlet.http;

import java.net.HttpCookie;

import javax.servlet.http.Cookie;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;

public class ServletCookieCodec implements Codec<Cookie, HttpCookie> {
	public static final ServletCookieCodec INSTANCE = new ServletCookieCodec();

	@Override
	public HttpCookie encode(Cookie cookie) throws EncodeException {
		if (cookie == null) {
			return null;
		}

		HttpCookie httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
		httpCookie.setComment(cookie.getComment());
		httpCookie.setSecure(cookie.getSecure());
		httpCookie.setHttpOnly(cookie.isHttpOnly());
		httpCookie.setMaxAge(cookie.getMaxAge());
		httpCookie.setDomain(cookie.getDomain());
		httpCookie.setPath(cookie.getPath());
		httpCookie.setVersion(cookie.getVersion());
		return httpCookie;
	}

	@Override
	public Cookie decode(HttpCookie source) throws DecodeException {
		if (source == null) {
			return null;
		}

		Cookie cookie = new Cookie(source.getName(), source.getValue());
		cookie.setComment(source.getComment());
		cookie.setDomain(source.getDomain());
		cookie.setHttpOnly(source.isHttpOnly());
		// TODO 整形溢出?
		cookie.setMaxAge((int) source.getMaxAge());
		cookie.setPath(source.getPath());
		cookie.setSecure(source.getSecure());
		cookie.setVersion(source.getVersion());
		return cookie;
	}

}

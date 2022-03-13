
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

		String comment = cookie.getComment();
		if (comment != null) {
			httpCookie.setComment(comment);
		}

		httpCookie.setSecure(cookie.getSecure());
		httpCookie.setHttpOnly(cookie.isHttpOnly());
		httpCookie.setMaxAge(cookie.getMaxAge());

		String domain = cookie.getDomain();
		if (domain != null) {
			httpCookie.setDomain(domain);
		}

		String path = cookie.getPath();
		if (path != null) {
			httpCookie.setPath(path);
		}

		httpCookie.setVersion(cookie.getVersion());
		return httpCookie;
	}

	@Override
	public Cookie decode(HttpCookie source) throws DecodeException {
		if (source == null) {
			return null;
		}

		Cookie cookie = new Cookie(source.getName(), source.getValue());
		
		String comment = source.getComment();
		if(comment != null) {
			cookie.setComment(comment);
		}
		
		String domain = source.getDomain();
		if(domain != null) {
			cookie.setDomain(domain);
		}
		
		cookie.setHttpOnly(source.isHttpOnly());
		// TODO 整形溢出?
		cookie.setMaxAge((int) source.getMaxAge());
		
		String path = source.getPath();
		if(path != null) {
			cookie.setPath(path);
		}
		
		cookie.setSecure(source.getSecure());
		cookie.setVersion(source.getVersion());
		return cookie;
	}

}

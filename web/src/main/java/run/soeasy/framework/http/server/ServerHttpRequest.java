package run.soeasy.framework.http.server;

import java.net.HttpCookie;

import run.soeasy.framework.http.HttpInputMessage;
import run.soeasy.framework.http.HttpPattern;
import run.soeasy.framework.http.HttpRequest;
import run.soeasy.framework.http.HttpSession;
import run.soeasy.framework.net.server.ServerRequest;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.collections.MultiValueMap;

public interface ServerHttpRequest extends HttpInputMessage, HttpRequest, ServerRequest {
	public static interface ServerHttpRequestWrapper<W extends ServerHttpRequest>
			extends ServerHttpRequest, HttpInputMessageWrapper<W>, HttpRequestWrapper<W>, ServerRequestWrapper<W> {
		@Override
		default String getPath() {
			return getSource().getPath();
		}

		@Override
		default String getContextPath() {
			return getSource().getContextPath();
		}

		@Override
		default HttpPattern getRequestPattern() {
			return getSource().getRequestPattern();
		}

		@Override
		default HttpSession getSession() {
			return getSource().getSession();
		}

		@Override
		default HttpSession getSession(boolean create) {
			return getSource().getSession(create);
		}

		@Override
		default String getIp() {
			return getSource().getIp();
		}

		@Override
		default MultiValueMap<String, String> getParameterMap() {
			return getSource().getParameterMap();
		}

		@Override
		default Elements<HttpCookie> getCookies() {
			return getSource().getCookies();
		}
	}

	String getPath();

	String getContextPath();

	@Override
	default HttpPattern getRequestPattern() {
		HttpPattern httpPattern = new HttpPattern();
		httpPattern.setPath(getPath());
		httpPattern.setMethod(getRawMethod());
		return httpPattern;
	}

	Elements<HttpCookie> getCookies();

	HttpSession getSession();

	HttpSession getSession(boolean create);

	String getIp();

	MultiValueMap<String, String> getParameterMap();
}

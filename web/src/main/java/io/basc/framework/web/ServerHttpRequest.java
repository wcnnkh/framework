package io.basc.framework.web;

import java.net.HttpCookie;

import io.basc.framework.http.HttpInputMessage;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.util.collections.MultiValueMap;

public interface ServerHttpRequest extends HttpInputMessage, HttpRequest, ServerRequest {
	String getPath();

	String getContextPath();

	HttpCookie[] getCookies();

	Session getSession();

	Session getSession(boolean create);

	String getIp();

	MultiValueMap<String, String> getParameterMap();
}

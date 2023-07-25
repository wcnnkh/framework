package io.basc.framework.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.security.Principal;

import io.basc.framework.http.HttpInputMessage;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.security.session.Session;
import io.basc.framework.util.attribute.EditableAttributes;
import io.basc.framework.util.collect.MultiValueMap;

public interface ServerHttpRequest extends HttpInputMessage, HttpRequest, EditableAttributes<String, Object> {
	String getPath();

	String getContextPath();

	HttpCookie[] getCookies();

	Session getSession();

	Session getSession(boolean create);

	boolean isSupportAsyncControl();

	ServerHttpAsyncControl getAsyncControl(ServerHttpResponse response);

	String getIp();

	BufferedReader getReader() throws IOException;

	InetSocketAddress getLocalAddress();

	InetSocketAddress getRemoteAddress();

	MultiValueMap<String, String> getParameterMap();

	Principal getPrincipal();
}

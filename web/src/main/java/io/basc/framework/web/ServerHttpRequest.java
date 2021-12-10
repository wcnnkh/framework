package io.basc.framework.web;

import io.basc.framework.http.HttpCookie;
import io.basc.framework.http.HttpInputMessage;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.security.session.Session;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.attribute.EditableAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Principal;

public interface ServerHttpRequest extends HttpInputMessage, HttpRequest, EditableAttributes<String, Object> {
	String getPath();

	String getContextPath();

	HttpCookie[] getCookies();

	Session getSession();

	Session getSession(boolean create);

	boolean isSupportAsyncControl();

	ServerHttpAsyncControl getAsyncControl(ServerHttpResponse response);

	/**
	 * 获取客户端请求的ip
	 * 
	 * @return
	 */
	String getIp();

	BufferedReader getReader() throws IOException;

	/**
	 * Return the address on which the request was received.
	 */
	InetSocketAddress getLocalAddress();

	/**
	 * Return the address of the remote client.
	 */
	InetSocketAddress getRemoteAddress();

	MultiValueMap<String, String> getParameterMap();

	Principal getPrincipal();
}

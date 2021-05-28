package scw.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Principal;

import scw.http.HttpCookie;
import scw.http.HttpInputMessage;
import scw.http.HttpRequest;
import scw.security.session.Session;
import scw.util.MultiValueMap;
import scw.util.attribute.Attributes;

public interface ServerHttpRequest extends HttpInputMessage, HttpRequest, Attributes<String, Object> {
	String getPath();
	
	String getContextPath();

	String getRawMethod();
   
	HttpCookie[] getCookies();

	Session getSession();

	Session getSession(boolean create);

	boolean isSupportAsyncControl();

	ServerHttpAsyncControl getAsyncControl(ServerHttpResponse response);
	
	/**
	 * 获取客户端请求的ip
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

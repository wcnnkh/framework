package scw.net.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Map;

import scw.net.http.HttpCookie;
import scw.net.http.HttpRequest;
import scw.net.message.InputMessage;
import scw.security.session.Session;
import scw.util.attribute.Attributes;

public interface ServerHttpRequest extends Attributes<String, Object>, InputMessage, HttpRequest {
	String getPath();
	
	String getRawContentType();

	String getContextPath();

	String getCharacterEncoding();

	BufferedReader getReader() throws IOException;

	/**
	 * Return the address on which the request was received.
	 */
	InetSocketAddress getLocalAddress();

	/**
	 * Return the address of the remote client.
	 */
	InetSocketAddress getRemoteAddress();
	
	String getRawMethod();

	HttpCookie[] getCookies();

	Session getSession();

	Session getSession(boolean create);

	Principal getPrincipal();
	
	boolean isSupportAsyncControl();

	ServerHttpAsyncControl getAsyncControl(ServerHttpResponse response);
	
	String getIp();
	
	String getParameter(String name);

	Enumeration<String> getParameterNames();

	String[] getParameterValues(String name);

	Map<String, String[]> getParameterMap();
}

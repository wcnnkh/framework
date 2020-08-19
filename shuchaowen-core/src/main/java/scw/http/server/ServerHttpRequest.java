package scw.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;

import scw.http.HttpCookie;
import scw.http.HttpInputMessage;
import scw.http.HttpRequest;
import scw.net.InetAddress;
import scw.security.session.Session;
import scw.util.MultiValueMap;
import scw.util.attribute.Attributes;

public interface ServerHttpRequest extends Attributes<String, Object>, HttpInputMessage, HttpRequest {
	String getPath();
	
	String getRawContentType();

	String getContextPath();

	String getCharacterEncoding();
	
	void setCharacterEncoding(String env) throws UnsupportedEncodingException;

	BufferedReader getReader() throws IOException;

	/**
	 * Return the address on which the request was received.
	 */
	InetAddress getLocalAddress();

	/**
	 * Return the address of the remote client.
	 */
	InetAddress getRemoteAddress();
	
	String getRawMethod();
   
	HttpCookie[] getCookies();

	Session getSession();

	Session getSession(boolean create);

	Principal getPrincipal();
	
	boolean isSupportAsyncControl();

	ServerHttpAsyncControl getAsyncControl(ServerHttpResponse response);
	
	String getIp();
	
	MultiValueMap<String, String> getParameterMap();
	
	MultiValueMap<String, String> getRestfulParameterMap();
}

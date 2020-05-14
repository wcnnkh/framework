package scw.net.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.Principal;

import scw.net.http.HttpCookie;
import scw.net.http.HttpRequest;
import scw.net.message.InputMessage;
import scw.security.session.Session;
import scw.util.MultiValueParameterFactory;
import scw.util.ip.IP;

public interface ServerHttpRequest extends InputMessage, HttpRequest, IP, MultiValueParameterFactory {
	String getController();
	
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

	HttpCookie getCookie(String name);

	HttpCookie[] getCookies();

	Session getSession();

	Session getSession(boolean create);

	Principal getPrincipal();
}

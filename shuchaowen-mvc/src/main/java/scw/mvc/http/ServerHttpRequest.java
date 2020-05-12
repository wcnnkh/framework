package scw.mvc.http;

import java.net.InetSocketAddress;
import java.security.Principal;

import scw.net.http.HttpCookie;
import scw.security.session.Session;
import scw.util.MultiValueParameterFactory;
import scw.util.ip.IP;

public interface ServerHttpRequest extends scw.net.http.HttpRequest, scw.mvc.ServerRequest, MultiValueParameterFactory, IP {
	String getRawMethod();

	HttpCookie getCookie(String name);
	
	HttpCookie[] getCookies();

	/**
	 * Returns the fully qualified name of the client or the last proxy that
	 * sent the request. If the engine cannot or chooses not to resolve the
	 * hostname (to improve performance), this method returns the dotted-string
	 * form of the IP address. For HTTP servlets, same as the value of the CGI
	 * variable <code>REMOTE_HOST</code>.
	 *
	 * @return a <code>String</code> containing the fully qualified name of the
	 *         client
	 */
	String getRemoteHost();

	Session getHttpSession();

	Session getHttpSession(boolean create);
	
	Principal getPrincipal();
	
	/**
	 * Return the address on which the request was received.
	 */
	InetSocketAddress getLocalAddress();

	/**
	 * Return the address of the remote client.
	 */
	InetSocketAddress getRemoteAddress();
}

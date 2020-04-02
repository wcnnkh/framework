package scw.mvc.http;

import scw.net.http.Cookie;
import scw.security.session.Session;
import scw.util.MultiValueParameterFactory;
import scw.util.ip.IP;

public interface HttpRequest extends scw.net.http.HttpRequest, scw.mvc.Request, MultiValueParameterFactory, IP {
	String getRawMethod();

	Cookie getCookie(String name, boolean ignoreCase);

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
}

package scw.mvc.http;

import scw.core.multivalue.MultiValueParameterFactory;
import scw.mvc.Request;
import scw.net.header.MultiValueHeadersReadOnly;
import scw.net.http.Cookie;
import scw.security.session.Session;
import scw.util.attribute.Attributes;
import scw.util.ip.IP;

public interface HttpRequest extends Attributes<String, Object>, MultiValueHeadersReadOnly, Request, MultiValueParameterFactory, IP {
	String getMethod();

	String getRequestPath();

	Cookie getCookie(String name, boolean ignoreCase);

	Session getHttpSession();

	Session getHttpSession(boolean create);

	long getDateHeader(String name);

	int getIntHeader(String name);

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

	boolean isAjax();

	String getContextPath();
}

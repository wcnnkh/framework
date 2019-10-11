package scw.mvc.http;

import scw.core.IP;
import scw.core.attribute.Attributes;
import scw.core.header.MultiValueHeadersReadOnly;
import scw.core.multivalue.MultiValueParameterFactory;
import scw.mvc.Request;
import scw.net.http.Cookie;
import scw.security.session.Session;

public interface HttpRequest extends Attributes<Object>, MultiValueHeadersReadOnly, Request, MultiValueParameterFactory, IP {
	String getMethod();

	String getRequestPath();

	Cookie getCookie(String name, boolean ignoreCase);

	Session getHttpSession();

	Session getHttpSession(boolean create);

	String getContentType();

	long getDateHeader(String name);

	int getIntHeader(String name);

	/**
	 * Returns the Internet Protocol (IP) address of the client or last proxy
	 * that sent the request. For HTTP servlets, same as the value of the CGI
	 * variable <code>REMOTE_ADDR</code>.
	 *
	 * @return a <code>String</code> containing the IP address of the client
	 *         that sent the request
	 */
	String getRemoteAddr();

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

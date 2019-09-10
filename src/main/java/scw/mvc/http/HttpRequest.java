package scw.mvc.http;

import java.util.Enumeration;
import java.util.Map;

import scw.mvc.AttributeManager;
import scw.mvc.Request;
import scw.net.http.Cookie;
import scw.session.Session;

public interface HttpRequest extends AttributeManager, Request{
	String getMethod();

	String getRequestPath();

	Cookie getCookie(String name, boolean ignoreCase);

	long getDateHeader(String name);

	String getHeader(String name);

	Enumeration<String> getHeaders(String name);

	Enumeration<String> getHeaderNames();

	int getIntHeader(String name);

	Session getHttpSession();

	Session getHttpSession(boolean create);

	String getContentType();

	String getParameter(String name);

	Enumeration<String> getParameterNames();

	String[] getParameterValues(String name);

	Map<String, String[]> getParameterMap();
	
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
    
    String getIP();
    
    boolean isAjax();
}

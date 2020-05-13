package scw.mvc.http;

import java.security.Principal;

import scw.net.http.HttpCookie;
import scw.security.session.Session;
import scw.util.MultiValueParameterFactory;
import scw.util.ip.IP;

public interface ServerHttpRequest extends scw.net.http.HttpRequest, scw.mvc.ServerRequest, MultiValueParameterFactory, IP {
	String getRawMethod();

	HttpCookie getCookie(String name);
	
	HttpCookie[] getCookies();

	Session getHttpSession();

	Session getHttpSession(boolean create);
	
	Principal getPrincipal();
}

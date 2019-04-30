package scw.core.net.http.client.cookie;

import java.util.Collection;

public interface CookieManager {

	Collection<Cookie> getCookies(String url);

	void setCookie(String url, Collection<Cookie> cookies);
}

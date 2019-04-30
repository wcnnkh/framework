package scw.core.net.http.client;

import java.net.URL;

public interface CookieManager {
	String getCookie(URL url);

	void setCookie(URL url, String cookies);
}

package scw.net;

import java.net.URLConnection;

public interface URLConnectionRequestCallback {
	
	void request(URLConnection urlConnection) throws Throwable;
	
}

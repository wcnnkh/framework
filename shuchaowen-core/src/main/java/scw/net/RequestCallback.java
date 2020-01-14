package scw.net;

import java.net.URLConnection;

public interface RequestCallback {
	
	void request(URLConnection urlConnection) throws Throwable;
	
}

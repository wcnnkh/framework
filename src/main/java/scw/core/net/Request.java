package scw.core.net;

import java.net.URLConnection;

public interface Request {
	
	void request(URLConnection urlConnection) throws Throwable;
	
}

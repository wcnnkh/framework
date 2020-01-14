package scw.net;

import java.net.URLConnection;

public interface URLConnectionResponseCallback<T> {

	T response(URLConnection urlConnection) throws Throwable;

}

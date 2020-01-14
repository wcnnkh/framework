package scw.net;

import java.net.URLConnection;

public interface ResponseCallback<T> {

	T response(URLConnection urlConnection) throws Throwable;

}

package scw.net;

import java.net.URLConnection;

public interface Response<T> {

	T response(URLConnection urlConnection) throws Throwable;

}

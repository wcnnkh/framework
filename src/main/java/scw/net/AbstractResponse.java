package scw.net;

import java.io.InputStream;
import java.net.URLConnection;

public abstract class AbstractResponse<T> implements Response<T> {

	public T response(URLConnection urlConnection) throws Throwable {
		InputStream is = null;
		try {
			return doInput(is);
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	public abstract T doInput(InputStream is) throws Throwable;
}

package scw.net;

import java.io.InputStream;
import java.net.URLConnection;

public abstract class AbstractResponse<T> implements Response<T> {

	public T response(URLConnection urlConnection) throws Throwable {
		if (urlConnection.getDoInput()) {
			InputStream is = null;
			try {
				is = urlConnection.getInputStream();
				return doInput(is);
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
		return null;
	}

	public abstract T doInput(InputStream is) throws Throwable;
}

package scw.net;

import java.io.InputStream;
import java.net.URLConnection;

public abstract class AbstractResponseCallback<T> implements URLConnectionResponseCallback<T> {

	public T response(URLConnection urlConnection) throws Throwable {
		if (urlConnection.getDoInput()) {
			InputStream is = null;
			try {
				is = urlConnection.getInputStream();
				return doInput(urlConnection, is);
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
		return null;
	}

	protected abstract T doInput(final URLConnection urlConnection, final InputStream is) throws Throwable;
}

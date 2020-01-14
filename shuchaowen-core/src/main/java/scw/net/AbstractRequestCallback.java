package scw.net;

import java.io.OutputStream;
import java.net.URLConnection;

public abstract class AbstractRequestCallback implements RequestCallback {

	public void request(URLConnection urlConnection) throws Throwable {
		if (urlConnection.getDoOutput()) {
			OutputStream os = null;
			try {
				os = urlConnection.getOutputStream();
				doOutput(urlConnection, os);
			} finally {
				if (os != null) {
					os.close();
				}
			}
		}
	}

	protected abstract void doOutput(final URLConnection urlConnection, final OutputStream os) throws Throwable;
}

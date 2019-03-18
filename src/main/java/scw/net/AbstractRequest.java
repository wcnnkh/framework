package scw.net;

import java.io.OutputStream;
import java.net.URLConnection;

public abstract class AbstractRequest implements Request {

	public void request(URLConnection urlConnection) throws Throwable {
		if (urlConnection.getDoOutput()) {
			OutputStream os = null;
			try {
				os = urlConnection.getOutputStream();
				doOutput(os);
			} finally {
				if (os != null) {
					os.close();
				}
			}
		}
	}

	public abstract void doOutput(OutputStream os) throws Throwable;
}

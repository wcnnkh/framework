package scw.net;

import java.net.HttpURLConnection;
import java.net.URLConnection;

public final class DefaultHttpMessageResponse implements Response<HttpMessage> {

	public HttpMessage response(URLConnection urlConnection) throws Throwable {
		if (urlConnection instanceof HttpURLConnection) {
			return new HttpURLConnectionMessage((HttpURLConnection) urlConnection);
		}
		return null;
	}
}

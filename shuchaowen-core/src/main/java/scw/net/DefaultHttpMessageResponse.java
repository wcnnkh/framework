package scw.net;

import java.net.HttpURLConnection;
import java.net.URLConnection;

import scw.net.message.HttpInputMessage;
import scw.net.message.HttpURLConnectionInputMessage;

public final class DefaultHttpMessageResponse implements ResponseCallback<HttpInputMessage> {

	public HttpInputMessage response(URLConnection urlConnection) throws Throwable {
		if (urlConnection instanceof HttpURLConnection) {
			return new HttpURLConnectionInputMessage((HttpURLConnection) urlConnection);
		}
		return null;
	}
}

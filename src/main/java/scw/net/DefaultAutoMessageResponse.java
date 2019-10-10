package scw.net;

import java.net.HttpURLConnection;
import java.net.URLConnection;

public final class DefaultAutoMessageResponse implements Response<Message> {

	public Message response(URLConnection urlConnection) throws Throwable {
		if (urlConnection instanceof HttpURLConnection) {
			return new HttpURLConnectionMessage((HttpURLConnection) urlConnection);
		}

		return new URLConnectionMessage(urlConnection);
	}

}

package scw.net;

import java.net.HttpURLConnection;
import java.net.URLConnection;

import scw.net.message.HttpURLConnectionInputMessage;
import scw.net.message.InputMessage;

public final class DefaultAutoMessageResponse implements ResponseCallback<InputMessage> {

	public InputMessage response(URLConnection urlConnection) throws Throwable {
		if (urlConnection instanceof HttpURLConnection) {
			return new HttpURLConnectionInputMessage((HttpURLConnection) urlConnection);
		}

		return new URLConnectionMessage(urlConnection);
	}

}

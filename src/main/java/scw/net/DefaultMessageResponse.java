package scw.net;

import java.net.URLConnection;

public final class DefaultMessageResponse implements Response<Message> {

	public Message response(URLConnection urlConnection) throws Throwable {
		return new URLConnectionMessage(urlConnection);
	}
}

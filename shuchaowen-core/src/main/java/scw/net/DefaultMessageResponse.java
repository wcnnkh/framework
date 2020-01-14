package scw.net;

import java.net.URLConnection;

import scw.net.message.InputMessage;

public final class DefaultMessageResponse implements ResponseCallback<InputMessage> {

	public InputMessage response(URLConnection urlConnection) throws Throwable {
		return new URLConnectionMessage(urlConnection);
	}
}

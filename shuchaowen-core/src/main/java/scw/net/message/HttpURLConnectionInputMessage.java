package scw.net.message;

import java.io.IOException;
import java.net.HttpURLConnection;

import scw.net.URLConnectionMessage;

public class HttpURLConnectionInputMessage extends URLConnectionMessage implements HttpInputMessage {
	private static final long serialVersionUID = 1L;
	private final int code;
	private final String message;

	public HttpURLConnectionInputMessage(HttpURLConnection httpURLConnection) throws IOException {
		super(httpURLConnection);
		this.code = httpURLConnection.getResponseCode();
		this.message = httpURLConnection.getResponseMessage();
	}

	public final int getCode() {
		return code;
	}

	public final String getMessage() {
		return message;
	}

}

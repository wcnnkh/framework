package scw.net;

import java.io.IOException;
import java.net.HttpURLConnection;

public class HttpURLConnectionMessage extends URLConnectionMessage implements HttpMessage {
	private static final long serialVersionUID = 1L;
	private final int code;
	private final String message;

	public HttpURLConnectionMessage(HttpURLConnection httpURLConnection) throws IOException {
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

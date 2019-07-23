package scw.net.http;

import java.io.OutputStream;
import java.net.URLConnection;

import scw.core.io.ByteArray;

public class BodyRequest extends HttpRequest {
	private final ByteArray body;

	public BodyRequest(Method method, String url, ByteArray body) {
		super(method, url);
		this.body = body;
	}

	@Override
	protected void doOutput(URLConnection urlConnection, OutputStream os) throws Throwable {
		body.writeTo(os);
	}
}

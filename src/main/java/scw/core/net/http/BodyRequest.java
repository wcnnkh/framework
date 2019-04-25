package scw.core.net.http;

import java.io.OutputStream;

import scw.core.ByteArray;
import scw.core.net.http.enums.Method;

public class BodyRequest extends HttpRequest {
	private final ByteArray body;

	public BodyRequest(Method method, String url, ByteArray body) {
		super(method, url);
		this.body = body;
	}

	@Override
	public void doOutput(OutputStream os) throws Throwable {
		body.writeTo(os);
	}
}

package scw.net.http;

import java.io.OutputStream;

import scw.common.ByteArray;
import scw.net.http.enums.Method;

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

package scw.net.http.request;

import java.io.OutputStream;

import scw.net.http.enums.Method;

public class BodyRequest extends HttpRequest {
	private final byte[] body;

	public BodyRequest(Method method, byte[] body) {
		super(method);
		this.body = body;
	}

	@Override
	public void doOutput(OutputStream os) throws Throwable {
		os.write(body);
	}
}

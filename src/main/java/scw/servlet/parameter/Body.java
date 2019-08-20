package scw.servlet.parameter;

import java.io.IOException;
import java.io.Serializable;

import scw.io.IOUtils;
import scw.servlet.Request;

public final class Body implements Serializable {
	private static final long serialVersionUID = 1L;
	private String body;

	public Body(Request request) throws IOException {
		this.body = IOUtils.read(request.getReader(), 0);
	}

	public String getBody() {
		return body;
	}

	@Override
	public String toString() {
		return getBody();
	}
}

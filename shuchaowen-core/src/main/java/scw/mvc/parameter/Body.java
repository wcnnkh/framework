package scw.mvc.parameter;

import java.io.IOException;

import scw.beans.annotation.Bean;
import scw.io.IOUtils;
import scw.mvc.Request;
import scw.mvc.http.HttpRequest;

@Bean(singleton = false)
public final class Body {
	private String body;
	private Request request;

	public Body(HttpRequest request) throws IOException {
		this.request = request;
	}

	public String getBody() {
		if (body == null) {
			try {
				body = IOUtils.read(request.getReader(), 0);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return body;
	}

	@Override
	public String toString() {
		return getBody();
	}
}

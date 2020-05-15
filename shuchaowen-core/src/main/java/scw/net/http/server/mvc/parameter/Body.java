package scw.net.http.server.mvc.parameter;

import java.io.IOException;

import scw.beans.annotation.Bean;
import scw.io.IOUtils;
import scw.net.http.server.ServerHttpRequest;

@Bean(singleton = false)
public final class Body {
	private String body;
	private ServerHttpRequest serverRequest;

	public Body(ServerHttpRequest request) throws IOException {
		this.serverRequest = request;
	}

	public String getBody() {
		if (body == null) {
			try {
				body = IOUtils.read(serverRequest.getReader(), 0);
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

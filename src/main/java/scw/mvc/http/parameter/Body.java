package scw.mvc.http.parameter;

import java.io.IOException;
import java.io.Serializable;

import scw.beans.annotation.Bean;
import scw.io.IOUtils;
import scw.mvc.http.HttpRequest;

@Bean(singleton=false)
public final class Body implements Serializable {
	private static final long serialVersionUID = 1L;
	private String body;

	public Body(HttpRequest request) throws IOException {
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

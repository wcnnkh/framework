package io.basc.framework.http;

import io.basc.framework.net.PathPattern;
import io.basc.framework.net.Request;
import io.basc.framework.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpPattern extends PathPattern {
	private String method;

	@Override
	public boolean test(Request request) {
		if (this.method != null) {
			if (!(request instanceof HttpRequest)) {
				return false;
			}

			HttpRequest httpRequest = (HttpRequest) request;
			if (!StringUtils.equals(httpRequest.getRawMethod(), this.method, true)) {
				return false;
			}
		}
		return super.test(request);
	}
}

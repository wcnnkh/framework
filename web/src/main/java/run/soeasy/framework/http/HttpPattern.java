package run.soeasy.framework.http;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.net.Request;
import run.soeasy.framework.net.uri.PathPattern;
import run.soeasy.framework.util.StringUtils;

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

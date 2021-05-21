package scw.mvc.security;

import scw.http.server.ServerHttpRequest;
import scw.http.server.pattern.HttpPatternRegistry;

public class LoginRequiredRegistry extends HttpPatternRegistry<Boolean> {

	public boolean isLoginRequried(ServerHttpRequest request) {
		Boolean v = get(request);
		if (v == null) {
			return false;
		}
		return v;
	}
}

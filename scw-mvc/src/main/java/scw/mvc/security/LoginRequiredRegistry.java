package scw.mvc.security;

import scw.web.ServerHttpRequest;
import scw.web.pattern.HttpPatterns;

public class LoginRequiredRegistry extends HttpPatterns<Boolean> {

	public boolean isLoginRequried(ServerHttpRequest request) {
		Boolean v = get(request);
		if (v == null) {
			return false;
		}
		return v;
	}
}

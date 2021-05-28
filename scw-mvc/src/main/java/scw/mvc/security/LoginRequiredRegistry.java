package scw.mvc.security;

import scw.web.ServerHttpRequest;
import scw.web.pattern.HttpPatternRegistry;

public class LoginRequiredRegistry extends HttpPatternRegistry<Boolean> {

	public boolean isLoginRequried(ServerHttpRequest request) {
		Boolean v = get(request);
		if (v == null) {
			return false;
		}
		return v;
	}
}

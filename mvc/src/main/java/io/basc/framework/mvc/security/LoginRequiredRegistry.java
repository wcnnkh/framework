package io.basc.framework.mvc.security;

import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.pattern.HttpPatterns;

public class LoginRequiredRegistry extends HttpPatterns<Boolean> {

	public boolean isLoginRequried(ServerHttpRequest request) {
		Boolean v = get(request);
		if (v == null) {
			return false;
		}
		return v;
	}
}

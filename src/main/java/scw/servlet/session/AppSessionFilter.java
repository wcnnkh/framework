package scw.servlet.session;

import scw.result.LoginResultFactory;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;

public class AppSessionFilter implements Filter {
	private final LoginResultFactory loginResultFactory;

	public AppSessionFilter(LoginResultFactory loginResultFactory) {
		this.loginResultFactory = loginResultFactory;
	}

	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable {
		AppSession session = request.getBean(AppSession.class);
		if (!session.isLogin()) {
			response.write(loginResultFactory.loginExpired());
			return;
		}
		filterChain.doFilter(request, response);
	}
}

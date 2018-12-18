package scw.servlet.session;

import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.action.Filter;
import scw.servlet.action.FilterChain;
import scw.servlet.view.common.Result;

public class WebSessionFilter implements Filter {

	public void doFilter(Request request, Response response,
			FilterChain filterChain) throws Throwable {
		WebSession session = request.getBean(WebSession.class);
		if (!session.isLogin()) {
			response.write(Result.loginExpired());
			return;
		}
		filterChain.doFilter(request, response);
	}
}

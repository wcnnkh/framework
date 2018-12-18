package scw.web.servlet.session;

import scw.web.servlet.Request;
import scw.web.servlet.Response;
import scw.web.servlet.action.Filter;
import scw.web.servlet.action.FilterChain;
import scw.web.servlet.view.common.Result;

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

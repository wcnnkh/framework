package scw.servlet.session;

import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.filter.Filter;
import scw.servlet.filter.FilterChain;
import scw.servlet.view.common.Result;

public class AppSessionFilter implements Filter {

	public void doFilter(Request request, Response response,
			FilterChain filterChain) throws Throwable {
		AppSession session = request.getBean(AppSession.class);
		if (!session.isLogin()) {
			response.write(Result.loginExpired());
			return;
		}
		filterChain.doFilter(request, response);
	}
}

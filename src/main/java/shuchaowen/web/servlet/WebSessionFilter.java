package shuchaowen.web.servlet;

import shuchaowen.web.servlet.action.Filter;
import shuchaowen.web.servlet.action.FilterChain;
import shuchaowen.web.servlet.view.common.Result;

public class WebSessionFilter implements Filter {

	public void doFilter(Request request, Response response,
			FilterChain filterChain) throws Throwable {
		WebSession webSession = request.getBean(WebSession.class);
		if (!webSession.isLogin()) {
			response.write(Result.loginExpired());
			return;
		}
		filterChain.doFilter(request, response);
	}

}

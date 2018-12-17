package shuchaowen.web.servlet.session;

import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.Response;
import shuchaowen.web.servlet.action.Filter;
import shuchaowen.web.servlet.action.FilterChain;
import shuchaowen.web.servlet.view.common.Result;

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

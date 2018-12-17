package shuchaowen.web.servlet;

import shuchaowen.web.servlet.action.Filter;
import shuchaowen.web.servlet.action.FilterChain;
import shuchaowen.web.servlet.view.common.Result;
import shuchaowen.web.servlet.view.common.enums.Code;

public class WebSessionFilter implements Filter {

	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable {
		WebSession webSession = request.getBean(WebSession.class);
		if (webSession == null) {
			response.write(new Result().setCode(Code.login_status_expired));
			return;
		}
		filterChain.doFilter(request, response);
	}

}

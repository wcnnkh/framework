package scw.servlet.session;

import scw.result.ResultFactory;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;

public class WebSessionFilter implements Filter {
	private final ResultFactory resultFactory;

	public WebSessionFilter(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void doFilter(Request request, Response response,
			FilterChain filterChain) throws Throwable {
		WebSession session = request.getBean(WebSession.class);
		if (!session.isLogin()) {
			response.write(resultFactory.authorizationFailure());
			return;
		}
		filterChain.doFilter(request, response);
	}
}

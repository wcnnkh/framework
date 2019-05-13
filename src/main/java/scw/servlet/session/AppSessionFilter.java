package scw.servlet.session;

import scw.result.ResultFactory;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;

public class AppSessionFilter implements Filter {
	private final ResultFactory resultFactory;

	public AppSessionFilter(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void doFilter(Request request, Response response,
			FilterChain filterChain) throws Throwable {
		AppSession session = request.getBean(AppSession.class);
		if (!session.isLogin()) {
			response.write(resultFactory.authorizationFailure());
			return;
		}
		filterChain.doFilter(request, response);
	}
}

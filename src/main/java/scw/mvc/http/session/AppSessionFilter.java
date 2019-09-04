package scw.mvc.http.session;

import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.result.ResultFactory;

public class AppSessionFilter extends HttpFilter {
	private final ResultFactory resultFactory;

	public AppSessionFilter(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest,
			HttpResponse httpResponse, scw.mvc.FilterChain chain) throws Throwable {
		AppSession session = channel.getBean(AppSession.class);
		if (!session.isLogin()) {
			return resultFactory.authorizationFailure();
		}

		return chain.doFilter(channel);
	}
}

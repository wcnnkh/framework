package scw.mvc.http.session;

import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.result.ResultFactory;

public class WebSessionFilter extends HttpFilter {
	private final ResultFactory resultFactory;

	public WebSessionFilter(ResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest,
			HttpResponse httpResponse, FilterChain chain) throws Throwable {
		WebSession session = channel.getBean(WebSession.class);
		if (!session.isLogin()) {
			return resultFactory.authorizationFailure();
		}
		
		return chain.doFilter(channel);
	}
}

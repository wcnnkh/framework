package scw.testing.http;

import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.action.http.HttpAction;
import scw.mvc.action.http.HttpActionFilter;
import scw.mvc.http.HttpChannel;
import scw.testing.TestingProducer;

public final class MyHttpTestingFilter extends HttpActionFilter {
	private TestingProducer<HttpTestingRequestMessage> producer;

	public MyHttpTestingFilter(TestingProducer<HttpTestingRequestMessage> producer) {
		this.producer = producer;
	}
	
	@Override
	protected Object doNoHttpFilter(Channel channel, Action action,
			ActionFilterChain chain) throws Throwable {
		return chain.doFilter(channel, action);
	}

	@Override
	protected Object doHttpFilter(HttpChannel channel, HttpAction action,
			ActionFilterChain chain) throws Throwable {
		producer.push(new SimpleHttpTestingRequestMessage(channel.getRequest()));
		return chain.doFilter(channel, action);
	}

}

package scw.testing.http;

import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.FilterChain;
import scw.mvc.action.http.HttpAction;
import scw.mvc.action.http.HttpFilter;
import scw.mvc.http.HttpChannel;
import scw.testing.TestingProducer;

public final class MyHttpTestingFilter extends HttpFilter {
	private TestingProducer<HttpTestingRequestMessage> producer;

	public MyHttpTestingFilter(TestingProducer<HttpTestingRequestMessage> producer) {
		this.producer = producer;
	}
	
	@Override
	protected Object doNoHttpFilter(Channel channel, Action action,
			FilterChain chain) throws Throwable {
		return chain.doFilter(channel, action);
	}

	@Override
	protected Object doHttpFilter(HttpChannel channel, HttpAction action,
			FilterChain chain) throws Throwable {
		producer.push(new SimpleHttpTestingRequestMessage(channel.getRequest()));
		return chain.doFilter(channel, action);
	}

}

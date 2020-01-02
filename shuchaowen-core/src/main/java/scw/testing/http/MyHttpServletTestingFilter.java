package scw.testing.http;

import scw.mvc.FilterChain;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.support.HttpFilter;
import scw.testing.TestingProducer;

public final class MyHttpServletTestingFilter extends HttpFilter {
	private TestingProducer<HttpTestingRequestMessage> producer;

	public MyHttpServletTestingFilter(TestingProducer<HttpTestingRequestMessage> producer) {
		this.producer = producer;
	}

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse, FilterChain chain)
			throws Throwable {
		producer.push(new SimpleHttpTestingRequestMessage(httpRequest));
		return chain.doFilter(channel);
	}

}

package scw.testing.http.servlet;

import javax.servlet.http.HttpServletRequest;

import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.http.HttpRequest;
import scw.testing.TestingProducer;
import scw.testing.http.HttpTestingRequestMessage;

public final class MyHttpServletTestingFilter implements Filter {
	private TestingProducer<HttpTestingRequestMessage> producer;

	public MyHttpServletTestingFilter(TestingProducer<HttpTestingRequestMessage> producer) {
		this.producer = producer;
	}

	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable {
		if (!(request instanceof HttpRequest)) {
			filterChain.doFilter(request, response);
			return;
		}

		producer.push(new ServletHttpTestingRequestMessage((HttpServletRequest) request));
	}

}

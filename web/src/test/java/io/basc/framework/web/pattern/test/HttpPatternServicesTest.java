package io.basc.framework.web.pattern.test;

import org.junit.Test;

import io.basc.framework.util.Pipeline;
import io.basc.framework.util.comparator.Ordered;
import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.WebException;
import io.basc.framework.web.cors.Cors;
import io.basc.framework.web.cors.CorsRegistry;
import io.basc.framework.web.pattern.ServerHttpRequestAccept;

public class HttpPatternServicesTest {
	@Test
	public void test() {
		CorsRegistry corsRegistry = new CorsRegistry();
		corsRegistry.add("/**", Cors.DEFAULT);
		corsRegistry.add("/abc/**", Cors.DEFAULT);
		corsRegistry.add(new Pipeline<ServerHttpRequest, Cors, WebException>() {

			@Override
			public Cors process(ServerHttpRequest source) throws WebException {
				return null;
			}

			@Override
			public String toString() {
				return "test1";
			}
		});

		corsRegistry.add(new TestProcessor("test12", 1));
		corsRegistry.add(new TestProcessor("test22", 1));
		System.out.println(corsRegistry);
	}

	private static class TestProcessor
			implements Pipeline<ServerHttpRequest, Cors, WebException>, ServerHttpRequestAccept, Ordered {
		private final String name;
		private final int order;

		public TestProcessor(String name, int order) {
			this.name = name;
			this.order = order;
		}

		@Override
		public int getOrder() {
			return order;
		}

		@Override
		public Cors process(ServerHttpRequest source) throws WebException {
			return null;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean test(ServerHttpRequest request) {
			return true;
		}
	}
}

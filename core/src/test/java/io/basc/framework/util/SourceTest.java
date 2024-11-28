package io.basc.framework.util;

import org.junit.Test;

public class SourceTest {
	@Test
	public void test() throws Throwable {
		Source<Object, Throwable> source = () -> {
			long t = System.currentTimeMillis();
			System.out.println("get:" + t);
			return t;
		};
		Pool<Object, Throwable> pool = source.onClose((e) -> System.out.println("close1:" + e));
		Channel<?, ?> channel = pool.newChannel().map((t) -> "-" + t).onClose((e) -> System.out.println("close2:" + e))
				.map((e) -> "-" + e).newChannel().newChannel().map((e) -> "-" + e)
				.onClose((e) -> System.out.println("close3:" + e)).onClose(() -> System.out.println("end"));
		System.out.println("-------1--------");
		System.out.println(channel.get());
		System.out.println("-------2--------");
		channel.close();
	}
}

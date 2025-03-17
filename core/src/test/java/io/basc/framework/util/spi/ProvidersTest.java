package io.basc.framework.util.spi;

import org.junit.Test;

import run.soeasy.framework.util.spi.Providers;

public class ProvidersTest {
	@Test
	public void test() {
		Providers<Object, RuntimeException> providers = new Providers<>();
		System.out.println("register1:" + providers.register(1).isCancellable());
		System.out.println("register2:" + providers.register(2).isCancellable());
		System.out.println(providers.toList());
		providers.optional().ifPresent((e) -> {
			System.out.println(e);
			providers.optional().ifPresent((e2) -> {
				System.out.println(e2);
				assert e != e2;
			});
		});
	}
}

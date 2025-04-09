package run.soeasy.framework.util.spi;

import org.junit.Test;

public class ProvidersTest {
	@Test
	public void test() {
		ServiceProvider<Object, RuntimeException> providers = new ServiceProvider<>();
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

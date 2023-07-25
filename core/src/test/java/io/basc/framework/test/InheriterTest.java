package io.basc.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executor;

import org.junit.Test;

import io.basc.framework.util.XUtils;
import io.basc.framework.util.transmittable.AnyInheriterRegistry;

public class InheriterTest {
	private ThreadLocal<Object> threadLocal = new ThreadLocal<>();
	private Executor executor = AnyInheriterRegistry.global().decorateExecutor(XUtils.getCommonExecutor());

	@Test
	public void test() {
		AnyInheriterRegistry.global().register(threadLocal);
		for (int i = 0; i < 10; i++) {
			executor.execute(() -> {
				System.out.println("1" + threadLocal.get());
				assertTrue(threadLocal.get() == null);
				threadLocal.set(XUtils.getUUID());
			});
		}

		String id = XUtils.getUUID();
		threadLocal.set(id);
		for (int i = 0; i < 10; i++) {
			executor.execute(() -> {
				System.out.println("2:" + threadLocal.get());
				assertEquals(id, threadLocal.get());
				threadLocal.set(XUtils.getUUID());
			});
		}
	}
}

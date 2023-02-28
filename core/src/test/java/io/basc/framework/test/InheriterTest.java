package io.basc.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.util.ThreadLocalInheriter;
import io.basc.framework.util.XUtils;

public class InheriterTest {
	private ThreadLocal<Object> threadLocal = new ThreadLocal<>();

	@Test
	public void test() {
		String id = XUtils.getUUID();
		threadLocal.set(id);
		System.out.println(threadLocal.get());
		start(XUtils.getInheriterRegistry().decorateRunnable(() -> {
			System.out.println("1:" + threadLocal.get());
			assertTrue(threadLocal.get() == null);
		}));
		
		ThreadLocalInheriter<Object> inheriter = new ThreadLocalInheriter<>(threadLocal);
		XUtils.getInheriterRegistry().register(inheriter);
		start(XUtils.getInheriterRegistry().decorateRunnable(() -> {
			System.out.println("2:" + threadLocal.get());
			assertEquals(id, threadLocal.get());
		}));
	}

	private void start(Runnable runnable) {
		new Thread(runnable).start();
	}
}

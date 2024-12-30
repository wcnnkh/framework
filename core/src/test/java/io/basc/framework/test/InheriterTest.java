package io.basc.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import io.basc.framework.util.sequences.uuid.UUIDSequences;
import io.basc.framework.util.transmittable.AnyInheriterRegistry;

public class InheriterTest {
	private ThreadLocal<Object> threadLocal = new ThreadLocal<>();
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private Executor executor = AnyInheriterRegistry.global().decorateExecutor(executorService);

	@Test
	public void test() {
		AnyInheriterRegistry.global().register(threadLocal);
		for (int i = 0; i < 10; i++) {
			executor.execute(() -> {
				System.out.println("1" + threadLocal.get());
				assertTrue(threadLocal.get() == null);
				threadLocal.set(UUIDSequences.getUUID());
			});
		}

		String id = UUIDSequences.getUUID();
		threadLocal.set(id);
		for (int i = 0; i < 10; i++) {
			executor.execute(() -> {
				System.out.println("2:" + threadLocal.get());
				assertEquals(id, threadLocal.get());
				threadLocal.set(UUIDSequences.getUUID());
			});
		}
	}
}

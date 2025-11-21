package run.soeasy.framework.core.transmittable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import run.soeasy.framework.core.RandomUtils;
import run.soeasy.framework.core.transmittable.registry.AnyInheriterRegistry;

public class InheriterTest {
	private ThreadLocal<Object> threadLocal = new ThreadLocal<>();
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private Executor executor = AnyInheriterRegistry.global().inheritable(executorService);

	@Test
	public void test() {
		AnyInheriterRegistry.global().register(threadLocal);
		for (int i = 0; i < 10; i++) {
			executor.execute(() -> {
				System.out.println("1" + threadLocal.get());
				assertTrue(threadLocal.get() == null);
				threadLocal.set(RandomUtils.uuid());
			});
		}

		String id = RandomUtils.uuid();
		threadLocal.set(id);
		for (int i = 0; i < 10; i++) {
			executor.execute(() -> {
				System.out.println("2:" + threadLocal.get());
				assertEquals(id, threadLocal.get());
				threadLocal.set(RandomUtils.uuid());
			});
		}
	}
}

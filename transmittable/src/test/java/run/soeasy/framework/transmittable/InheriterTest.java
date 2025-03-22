package run.soeasy.framework.transmittable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import run.soeasy.framework.util.sequences.uuid.UUIDSequences;

public class InheriterTest {
	private ThreadLocal<Object> threadLocal = new ThreadLocal<>();
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private Executor executor = AnyInheriterRegistry.global().wrapExecutor(executorService);

	@Test
	public void test() {
		AnyInheriterRegistry.global().register(threadLocal);
		for (int i = 0; i < 10; i++) {
			executor.execute(() -> {
				System.out.println("1" + threadLocal.get());
				assertTrue(threadLocal.get() == null);
				threadLocal.set(UUIDSequences.global().next());
			});
		}

		String id = UUIDSequences.global().next();
		threadLocal.set(id);
		for (int i = 0; i < 10; i++) {
			executor.execute(() -> {
				System.out.println("2:" + threadLocal.get());
				assertEquals(id, threadLocal.get());
				threadLocal.set(UUIDSequences.global().next());
			});
		}
	}
}

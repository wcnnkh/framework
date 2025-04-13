package run.soeasy.framework.util;

import org.junit.Test;

import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.Pool;
import run.soeasy.framework.core.function.Supplier;

public class SourceTest {
	@Test
	public void test() throws Throwable {
		Supplier<Object, Throwable> source = () -> {
			long t = System.currentTimeMillis();
			System.out.println("get:" + t);
			return t;
		};

		source.onClose((e) -> System.out.println("映射结束")).newPipeline().map((e) -> {
			System.out.println("进行映射");
			return e;
		}).optional().isPresent();

		Pool<Object, Throwable> pool = source.onClose((e) -> System.out.println("close1:" + e));
		Pipeline<?, ?> channel = pool.newPipeline().map((t) -> "-" + t)
				.onClose((e) -> System.out.println("close2:" + e)).map((e) -> "-" + e).newPipeline().newPipeline()
				.map((e) -> "-" + e).onClose((e) -> System.out.println("close3:" + e))
				.onClose(() -> System.out.println("end"));
		System.out.println("-------1--------");
		System.out.println(channel.get());
		System.out.println("-------2--------");
		channel.close();
	}
}

package run.soeasy.framework.core.function;

import org.junit.Test;

public class FunctionTest {
	@Test
	public void test() throws Throwable {
		ThrowingSupplier<Object, Throwable> source = () -> {
			long t = System.currentTimeMillis();
			System.out.println("get:" + t);
			return t;
		};

		source.onClose((e) -> System.out.println("映射结束")).map((e) -> {
			System.out.println("进行映射");
			return String.valueOf(e);
		}).onClose((e) -> System.out.println("e:" + e)).closeable().autoCloseable().get();
		
		System.out.println("--------");
		Pipeline<String, Throwable> pipeline = source.onClose((e) -> System.out.println("close1:" + e))
				.map((e) -> "-" + e + "-").onClose((e) -> System.out.println("close2:" + e)).closeable();
		System.out.println(pipeline.get());
		System.out.println("--------");
		pipeline.close();
		System.out.println("------------------开始---------------");
		source = source.onClose((e) -> System.out.println("close1:" + e));
		Pipeline<?, ?> channel = source.map((t) -> "-" + t).onClose((e) -> System.out.println("close2:" + e))
				.map((e) -> "-" + e).onClose((e) -> System.out.println("closeable:" + e)).map((e) -> "-" + e).onClose((e) -> System.out.println("close3:" + e))
				.onClose(() -> System.out.println("end")).onClose((e) -> System.out.println("end:" + e));
		System.out.println("-------1--------");
		System.out.println(channel.get());
		System.out.println("-------2--------");
		channel.close();
	}
}

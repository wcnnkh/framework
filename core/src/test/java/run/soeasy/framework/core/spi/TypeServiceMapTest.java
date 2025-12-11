package run.soeasy.framework.core.spi;

import org.junit.Test;

import run.soeasy.framework.core.RandomUtils;

public class TypeServiceMapTest {
	@Test
	public void test() {
		TypeServiceMap<String> serviceMap = new TypeServiceMap<>();
		serviceMap.register(String.class, "str-" + RandomUtils.uuid());
		serviceMap.register(Number.class, "Number-" + RandomUtils.uuid());
		serviceMap.register(Integer.class, "int-" + RandomUtils.uuid());
		serviceMap.register(Long.class, "long-" + RandomUtils.uuid());
		System.out.println(serviceMap.toString());

		serviceMap.assignableFrom(String.class).forEach((e) -> System.out.println("String:" + e));
		serviceMap.assignableFrom(Number.class).forEach((e) -> System.out.println("number:" + e));
	}
}

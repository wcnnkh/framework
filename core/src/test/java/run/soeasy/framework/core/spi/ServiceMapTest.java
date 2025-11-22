package run.soeasy.framework.core.spi;

import org.junit.Test;

import run.soeasy.framework.core.RandomUtils;

public class ServiceMapTest {
	@Test
	public void test() {
		ServiceMap<String> serviceMap = new ServiceMap<>();
		serviceMap.set(String.class, "str-" + RandomUtils.uuid());
		serviceMap.set(Number.class, "Number-" + RandomUtils.uuid());
		serviceMap.set(Integer.class, "int-" + RandomUtils.uuid());
		serviceMap.set(Long.class, "long-" + RandomUtils.uuid());
		System.out.println(serviceMap.toString());

		serviceMap.assignableFrom(String.class).forEach((e) -> System.out.println("String:" + e));
		serviceMap.assignableFrom(Number.class).forEach((e) -> System.out.println("number:" + e));
	}
}

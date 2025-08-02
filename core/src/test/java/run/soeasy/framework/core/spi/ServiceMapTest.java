package run.soeasy.framework.core.spi;

import org.junit.Test;

import run.soeasy.framework.sequences.UUIDSequence;

public class ServiceMapTest {
	@Test
	public void test() {
		ServiceMap<String> serviceMap = new ServiceMap<>();
		serviceMap.set(String.class, "str-" + UUIDSequence.random().next());
		serviceMap.set(Number.class, "Number-" + UUIDSequence.random().next());
		serviceMap.set(Integer.class, "int-" + UUIDSequence.random().next());
		serviceMap.set(Long.class, "long-" + UUIDSequence.random().next());
		System.out.println(serviceMap.toString());

		serviceMap.assignableFrom(String.class).forEach((e) -> System.out.println("String:" + e));
		serviceMap.assignableFrom(Number.class).forEach((e) -> System.out.println("number:" + e));
	}
}

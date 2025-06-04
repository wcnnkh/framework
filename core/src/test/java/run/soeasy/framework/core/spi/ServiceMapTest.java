package run.soeasy.framework.core.spi;

import org.junit.Test;

import run.soeasy.framework.sequences.uuid.UUIDSequences;

public class ServiceMapTest {
	@Test
	public void test() {
		ServiceMap<String> serviceMap = new ServiceMap<>();
		serviceMap.set(String.class, "str-" + UUIDSequences.global().next());
		serviceMap.set(Number.class, "Number-" + UUIDSequences.global().next());
		serviceMap.set(Integer.class, "int-" + UUIDSequences.global().next());
		serviceMap.set(Long.class, "long-" + UUIDSequences.global().next());
		System.out.println(serviceMap.toString());

		serviceMap.search(String.class).forEach((e) -> System.out.println("String:" + e));
		serviceMap.search(Number.class).forEach((e) -> System.out.println("number:" + e));
	}
}

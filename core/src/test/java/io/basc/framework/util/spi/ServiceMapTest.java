package io.basc.framework.util.spi;

import org.junit.Test;

import io.basc.framework.util.sequences.uuid.UUIDSequences;

public class ServiceMapTest {
	@Test
	public void test() {
		ServiceMap<String> serviceMap = new ServiceMap<>();
		serviceMap.set(String.class, "str-" + UUIDSequences.getUUID());
		serviceMap.set(Number.class, "Number-" + UUIDSequences.getUUID());
		serviceMap.set(Integer.class, "int-" + UUIDSequences.getUUID());
		serviceMap.set(Long.class, "long-" + UUIDSequences.getUUID());
		System.out.println(serviceMap.toString());

		serviceMap.search(String.class).forEach((e) -> System.out.println("String:" + e));
		serviceMap.search(Number.class).forEach((e) -> System.out.println("number:" + e));
	}
}

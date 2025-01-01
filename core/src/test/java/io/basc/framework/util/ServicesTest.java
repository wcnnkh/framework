package io.basc.framework.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.ListContainer;
import io.basc.framework.util.sequences.uuid.RandomUUIDSequence;

public class ServicesTest {
	@Test
	public void test() {
		ListContainer<String, List<ElementRegistration<String>>> container = new ListContainer<>(ArrayList::new);
		container.register(RandomUUIDSequence.getUUID());
		System.out.println(container.toList().toString());
		container.forEach((e) -> System.out.println(e));
	}
}

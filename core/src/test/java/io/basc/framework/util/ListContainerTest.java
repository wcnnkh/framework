package io.basc.framework.util;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.ListContainer;
import io.basc.framework.util.sequences.uuid.RandomUUIDSequence;

public class ListContainerTest {
	@Test
	public void test() {
		ListContainer<String, List<ElementRegistration<String>>> container = new ListContainer<>(ArrayList::new);
		Registration registration = container.register(RandomUUIDSequence.getUUID());
		assertFalse(registration.isCancelled());
	}
}

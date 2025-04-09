package run.soeasy.framework.util;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.register.container.ElementRegistration;
import run.soeasy.framework.util.register.container.ListContainer;
import run.soeasy.framework.util.sequences.uuid.RandomUUIDSequence;

public class ListContainerTest {
	@Test
	public void test() {
		ListContainer<String, List<ElementRegistration<String>>> container = new ListContainer<>(ArrayList::new);
		Registration registration = container.register(RandomUUIDSequence.getUUID());
		assertFalse(registration.isCancelled());
	}
}

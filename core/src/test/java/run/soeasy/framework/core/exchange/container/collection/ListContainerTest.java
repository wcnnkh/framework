package run.soeasy.framework.core.exchange.container.collection;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.sequences.UUIDSequence;

public class ListContainerTest {
	@Test
	public void test() {
		ListContainer<String, List<ElementRegistration<String>>> container = new ListContainer<>(ArrayList::new);
		Registration registration = container.register(UUIDSequence.random().next());
		assertFalse(registration.isCancelled());
	}
}

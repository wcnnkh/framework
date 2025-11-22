package run.soeasy.framework.core.exchange.container.collection;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import run.soeasy.framework.core.RandomUtils;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.ElementRegistration;

public class ListContainerTest {
	@Test
	public void test() {
		ListContainer<String, List<ElementRegistration<String>>> container = new ListContainer<>(ArrayList::new);
		Registration registration = container.register(RandomUtils.uuid());
		assertFalse(registration.isCancelled());
	}
}

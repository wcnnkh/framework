package io.basc.framework.autoconfigure.context.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import io.basc.framework.autoconfigure.beans.factory.Bean;
import io.basc.framework.autoconfigure.beans.factory.Component;
import io.basc.framework.autoconfigure.boot.BootApplication;
import io.basc.framework.autoconfigure.context.ImportResource;
import io.basc.framework.boot.Application;
import io.basc.framework.context.ApplicationContext;

@Component
@ImportResource("test.properties")
@BootApplication
public class BootTest {
	private int value = 1;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Test
	public void test() throws InterruptedException, ExecutionException {
		ApplicationContext application = Application.run(BootTest.class);

		assertTrue(application.getPort().getAsInt() == 8888);

		BootTest context1 = application.getBean(BootTest.class);
		assertTrue(context1.getValue() == 1);

		BootTest context2 = application.getBean("testBean", BootTest.class);
		assertTrue(context2.getValue() == 2);
	}

	@Bean("testBean")
	public BootTest getBootTest() {
		return getBootTest(2);
	}

	public BootTest getBootTest(int value) {
		BootTest test = new BootTest();
		test.setValue(value);
		return test;
	}
}

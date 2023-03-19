package io.basc.framework.boot.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.annotation.BootApplication;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.context.annotation.Bean;
import io.basc.framework.context.annotation.Component;
import io.basc.framework.context.annotation.ImportResource;

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
		Application application = MainApplication.run(BootTest.class).get();

		assertTrue(application.getPort().getAsInt() == 8888);

		BootTest context1 = (BootTest) application.getInstance(BootTest.class);
		assertTrue(context1.getValue() == 1);

		BootTest context2 = (BootTest) application.getInstance("testBean");
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

package io.basc.framework.boot.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.ApplicationUtils;
import io.basc.framework.boot.support.MainApplication;

public class BootTest {
	@Test
	public void test() throws InterruptedException, ExecutionException{
		Application application = MainApplication.run(BootTest.class).get();
		System.out.println(application.getEnvironment().getString(ApplicationUtils.SERVER_PORT_PROPERTY));
		assertTrue("8888".equals(application.getEnvironment().getString(ApplicationUtils.SERVER_PORT_PROPERTY)));
	}
}

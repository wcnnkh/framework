package io.basc.framework.boot.test;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

public class BootTest {
	@Test
	public void test() throws InterruptedException, ExecutionException{
		Application application = MainApplication.run(BootTest.class).get();
		System.out.println(application.getEnvironment().getString("server.port"));
	}
}

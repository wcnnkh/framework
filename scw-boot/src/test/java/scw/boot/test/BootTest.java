package scw.boot.test;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import scw.boot.Application;
import scw.boot.support.MainApplication;

public class BootTest {
	@Test
	public void test() throws InterruptedException, ExecutionException{
		Application application = MainApplication.run(BootTest.class).get();
		System.out.println(application.getEnvironment().getString("server.port"));
	}
}

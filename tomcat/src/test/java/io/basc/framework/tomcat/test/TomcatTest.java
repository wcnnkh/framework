package io.basc.framework.tomcat.test;

import static org.junit.Assert.assertFalse;

import java.util.concurrent.ExecutionException;

import org.junit.Test;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.net.InetUtils;

public class TomcatTest {
	@Test
	public void test() throws InterruptedException, ExecutionException {
		int port = InetUtils.getAvailablePort();
		Application application = MainApplication.run(TomcatTest.class, new String[] {"-p", port + ""}).get();
		assertFalse(InetUtils.isAvailablePort(port));
		application.destroy();
	}
}

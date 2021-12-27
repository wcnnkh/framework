package io.basc.framework.rpc.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.support.MainApplication;
import io.basc.framework.logger.Levels;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.InetUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.web.pattern.annotation.RequestMapping;

@RequestMapping
public class HttpRemoteTest {
	private static String index = XUtils.getUUID();

	@Test
	public void test() throws Throwable {
		LoggerFactory.getLevelManager().getCustomLevelRegistry().put("io.basc.framework.rpc", Levels.DEBUG.getValue());
		Application application = MainApplication
				.run(HttpRemoteTest.class, new String[] { "-p", InetUtils.getAvailablePort() + "" }).get();
		TestRemoteInterface test = application.getInstance(TestRemoteInterface.class);
		String response = test.index();
		System.out.println(response);
		assertTrue(index.equals(response));
	}

	@RequestMapping
	public String index() {
		return index;
	}
}

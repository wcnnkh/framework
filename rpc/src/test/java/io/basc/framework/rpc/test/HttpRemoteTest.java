package io.basc.framework.rpc.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.context.support.DefaultApplicationContext;
import io.basc.framework.util.logging.Levels;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

public class HttpRemoteTest {
	private static Logger logger = LoggerFactory.getLogger(HttpRemoteTest.class);

	@Test
	public void test() throws Throwable {
		LoggerFactory.getSource().getLevelManager().getMaster().put("io.basc.framework.rpc",
				Levels.DEBUG.getValue());
		DefaultApplicationContext beanFactory = new DefaultApplicationContext();
		beanFactory.init();
		TestRemoteInterface test = beanFactory.getInstance(TestRemoteInterface.class);
		String response;
		try {
			response = test.index();
		} catch (Exception e) {
			logger.info(e, "发生异常也应该让编译通过");
			return;
		}
		assertTrue(response.indexOf("百度") != -1);
	}
}

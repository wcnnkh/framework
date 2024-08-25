package io.basc.framework.context.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.context.ApplicationContext;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.context.support.ContextLoader;
import io.basc.framework.context.support.GenericApplicationContext;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

public class ApplicationContextTest {
	private static Logger logger = LoggerFactory.getLogger(ApplicationContextTest.class);

	@Test
	public void test() {
		ConfigurableApplicationContext configurableApplicationContext = new GenericApplicationContext();
		try {
			configurableApplicationContext.start();
			logger.info("running");

			ApplicationContext applicationContext = ContextLoader.getCurrentApplicationContext();
			assertTrue(applicationContext == configurableApplicationContext);
		} finally {
			configurableApplicationContext.close();
		}
	}
}

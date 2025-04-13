package run.soeasy.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.logging.JdkLoggerFactory;
import run.soeasy.framework.logging.Logger;

public class JdkLoggerTest {
	@Test
	public void test() {
		JdkLoggerFactory loggerFactory = new JdkLoggerFactory();
		Logger logger = loggerFactory.getLogger(JdkLoggerTest.class.getName());
		logger.info("AAA");
		assertTrue(logger.isInfoEnabled());
	}
}

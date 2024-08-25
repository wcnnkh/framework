package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.util.logging.JdkLoggerFactory;
import io.basc.framework.util.logging.Logger;

public class JdkLoggerTest {
	@Test
	public void test() {
		JdkLoggerFactory loggerFactory = new JdkLoggerFactory();
		Logger logger = loggerFactory.getLogger(JdkLoggerTest.class.getName());
		logger.info("AAA");
		assertTrue(logger.isInfoEnabled());
	}
}

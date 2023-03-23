package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.logger.JdkLoggerFactory;
import io.basc.framework.logger.Logger;

public class JdkLoggerTest {
	@Test
	public void test() {
		JdkLoggerFactory loggerFactory = new JdkLoggerFactory();
		Logger logger = loggerFactory.getLogger(JdkLoggerTest.class.getName());
		logger.info("AAA");
		assertTrue(logger.isInfoEnabled());
	}
}

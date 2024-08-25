package io.basc.framework.slf4j.test;

import org.junit.Test;

import io.basc.framework.util.logging.JdkLoggerFactory;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

public class Slf4jTest {
	private static Logger logger = LoggerFactory.getLogger(Slf4jTest.class);

	@Test
	public void test() {
		logger.info("This is info message");
		logger.debug("This is debug message");
		LoggerFactory.getSource().setLoggerFactory(new JdkLoggerFactory());
		logger.warn("This is warn message");
		logger.error("This is error message");
	}
}

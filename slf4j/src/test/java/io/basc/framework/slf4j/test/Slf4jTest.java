package io.basc.framework.slf4j.test;

import org.junit.Test;

import run.soeasy.framework.util.logging.JdkLoggerFactory;
import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;

public class Slf4jTest {
	private static Logger logger = LogManager.getLogger(Slf4jTest.class);

	@Test
	public void test() {
		logger.info("This is info message");
		logger.debug("This is debug message");
		LogManager.getConfigurable().setLoggerFactory(new JdkLoggerFactory());
		logger.warn("This is warn message");
		logger.error("This is error message");
	}
}

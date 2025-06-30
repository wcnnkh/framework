package io.basc.framework.slf4j.test;

import org.junit.Test;

import run.soeasy.framework.logging.JdkLoggerFactory;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;
import run.soeasy.framework.slf4j.Slf4jLoggerFactory;

public class Slf4jTest {
	private static Logger logger = LogManager.getLogger(Slf4jTest.class);

	@Test
	public void test() {
		assert LogManager.getConfigurable().getLoggerFactory() instanceof Slf4jLoggerFactory;

		logger.info("This is info message");
		logger.debug("This is debug message");
		LogManager.getConfigurable().setLoggerFactory(new JdkLoggerFactory());
		logger.warn("This is warn message");
		logger.error("This is error message");
	}
}

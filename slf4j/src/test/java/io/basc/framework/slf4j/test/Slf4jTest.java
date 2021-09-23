package io.basc.framework.slf4j.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.slf4j.Slf4jLogger;

public class Slf4jTest {
	private static Logger logger = LoggerFactory.getLogger(Slf4jTest.class);
	
	@Test
	public void test() {
		assertTrue(logger instanceof Slf4jLogger);
		logger.trace("This is trace message");
		logger.info("This is info message");
		logger.debug("This is debug message");
		logger.warn("This is warn message");
		logger.error("This is error message");
	}
}

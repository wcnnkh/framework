package io.basc.framework.test;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import java.util.logging.Level;

import org.junit.Test;

public class LoggerTest {
	@Test
	public void test() {
		Logger logger = LoggerFactory.getLogger(LoggerTest.class);
		logger.info("message: {}", "a");
		logger.debug("debug message: {}", "b");
		logger.warn("error message: {}", "c");
		logger.log(Level.INFO, "abc", "b");
	}
}

package scw.test;

import java.util.logging.Level;

import org.junit.Test;

import scw.logger.CustomLogger;

public class LoggerTest {
	@Test
	public void test() {
		CustomLogger logger = CustomLogger.getLogger(LoggerTest.class);
		logger.info("message: {}", "a");
		logger.debug("debug message: {}", "b");
		logger.warn("error message: {}", "c");
		logger.log(Level.INFO, "abc", "b");
	}
}

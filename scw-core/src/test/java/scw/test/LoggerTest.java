package scw.test;

import java.util.logging.Level;

import org.junit.Test;

import scw.logger.CustomLogger;

public class LoggerTest {
	@Test
	public void test() {
		CustomLogger logger = CustomLogger.getLogger(LoggerTest.class);
		logger.setLevel(Level.ALL);
		logger.info("message: {}", "abc");
		logger.debug("message: {}", "abc");
		logger.warn("error message: {}");
	}
}

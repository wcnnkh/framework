package scw.test;

import java.util.logging.Level;

import org.junit.Test;

import scw.logger.Logger;
import scw.logger.LoggerFactory;

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

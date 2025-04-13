package run.soeasy.framework.test;

import java.util.logging.Level;

import org.junit.Test;

import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

public class LoggerTest {
	@Test
	public void test() {
		Logger logger = LogManager.getLogger(LoggerTest.class);
		logger.info("message: {}", "a");
		logger.debug("debug message: {}", "b");
		logger.warn("error message: {}", "c");
		logger.log(Level.INFO, "abc", "b");
	}
}

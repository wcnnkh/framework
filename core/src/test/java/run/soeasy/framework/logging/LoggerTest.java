package run.soeasy.framework.logging;

import java.util.logging.Level;

import org.junit.Test;

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

package scw.test;

import org.junit.Test;

import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class LoggerLevelTest {
	@Test
	public void test() {
		Logger logger = LoggerFactory.getLogger(LoggerLevelTest.class);
		logger.debug("logger level test");
	}
}

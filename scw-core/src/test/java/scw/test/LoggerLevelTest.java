package scw.test;

import org.junit.Test;

import scw.logger.Levels;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class LoggerLevelTest {
	@Test
	public void test() {
		LoggerFactory.getLevelManager().getCustomLevelRegistry().put(LoggerLevelTest.class.getName(), Levels.DEBUG.getValue());
		Logger logger = LoggerFactory.getLogger(LoggerLevelTest.class);
		logger.debug("debug info");
		logger.trace("trace info");
		LoggerFactory.getLevelManager().getCustomLevelRegistry().put(LoggerLevelTest.class.getName(), Levels.TRACE.getValue());
		logger.debug("debug info");
		logger.trace("trace info");
	}
}

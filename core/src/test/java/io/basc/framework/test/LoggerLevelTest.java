package io.basc.framework.test;

import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import org.junit.Test;

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

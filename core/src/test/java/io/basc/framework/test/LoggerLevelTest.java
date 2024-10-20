package io.basc.framework.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.util.logging.Levels;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class LoggerLevelTest {
	@Test
	public void test() {
		LogManager.getSource().getLevelManager().put(LoggerLevelTest.class.getName(), Levels.DEBUG.getValue());
		Logger logger = LogManager.getLogger(LoggerLevelTest.class);
		assertTrue(logger.isDebugEnabled());
		assertFalse(logger.isTraceEnabled());
		logger.debug("debug info");
		logger.trace("trace info");
		LogManager.getSource().getLevelManager().put(LoggerLevelTest.class.getName(), Levels.TRACE.getValue());
		assertTrue(logger.isDebugEnabled());
		assertTrue(logger.isTraceEnabled());
		logger.debug("debug info");
		logger.trace("trace info");
	}
}

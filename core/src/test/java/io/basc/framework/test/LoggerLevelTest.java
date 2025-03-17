package io.basc.framework.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;

public class LoggerLevelTest {
	@Test
	public void test() {
		// TODO
		// LogManager.getSource().getLevelManager().put(LoggerLevelTest.class.getName(),
		// Levels.DEBUG.getValue());
		Logger logger = LogManager.getLogger(LoggerLevelTest.class);
		assertTrue(logger.isDebugEnabled());
		assertFalse(logger.isTraceEnabled());
		logger.debug("debug info");
		logger.trace("trace info");
		// TODO
		// LogManager.getSource().getLevelManager().put(LoggerLevelTest.class.getName(),
		// Levels.TRACE.getValue());
		assertTrue(logger.isDebugEnabled());
		assertTrue(logger.isTraceEnabled());
		logger.debug("debug info");
		logger.trace("trace info");
	}
}

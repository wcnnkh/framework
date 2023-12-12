package io.basc.framework.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.logger.Levels;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public class LoggerLevelTest {
	@Test
	public void test() {
		LoggerFactory.getSource().getLevelManager().put(LoggerLevelTest.class.getName(), Levels.DEBUG.getValue());
		Logger logger = LoggerFactory.getLogger(LoggerLevelTest.class);
		assertTrue(logger.isDebugEnabled());
		assertFalse(logger.isTraceEnabled());
		logger.debug("debug info");
		logger.trace("trace info");
		LoggerFactory.getSource().getLevelManager().put(LoggerLevelTest.class.getName(), Levels.TRACE.getValue());
		assertTrue(logger.isDebugEnabled());
		assertTrue(logger.isTraceEnabled());
		logger.debug("debug info");
		logger.trace("trace info");
	}
}

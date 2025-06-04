package run.soeasy.framework.logging;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LoggerLevelTest {
	@Test
	public void test() {
		LogManager.getConfigurable().setLevel(LoggerLevelTest.class.getName(), Levels.DEBUG.getValue());
		Logger logger = LogManager.getLogger(LoggerLevelTest.class);
		assertTrue(logger.isDebugEnabled());
		assertFalse(logger.isTraceEnabled());
		logger.debug("debug info");
		logger.trace("trace info");
		LogManager.getConfigurable().setLevel(LoggerLevelTest.class.getName(), Levels.TRACE.getValue());
		assertTrue(logger.isDebugEnabled());
		assertTrue(logger.isTraceEnabled());
		logger.debug("debug info");
		logger.trace("trace info");
	}
}

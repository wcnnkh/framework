package io.basc.framework.log4j2.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.basc.framework.util.XUtils;
import io.basc.framework.util.logging.CustomLevel;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class Log4j2Test {
	@Test
	public void test() {
		LogManager.getSource().getLevelManager().put("io.basc.framework.log4j2", CustomLevel.DEBUG);
		Logger logger = LogManager.getLogger(Log4j2Test.class.getName());
		// assertTrue(logger.isInfoEnabled());
		logger.info(XUtils.getUUID());
		logger.debug(XUtils.getUUID());
		logger.warn(XUtils.getUUID());
		logger.error(XUtils.getUUID());
		logger.trace(XUtils.getUUID());
		assertTrue(logger.isDebugEnabled());
	}
}

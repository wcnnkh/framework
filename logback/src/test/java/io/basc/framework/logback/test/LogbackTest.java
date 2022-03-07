package io.basc.framework.logback.test;

import org.junit.Test;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.XUtils;

public class LogbackTest {
	@Test
	public void test() {
		Logger logger = LoggerFactory.getLogger(LogbackTest.class);
		logger.info(XUtils.getUUID());
		logger.debug(XUtils.getUUID());
		logger.warn(XUtils.getUUID());
		logger.error(XUtils.getUUID());
		logger.trace(XUtils.getUUID());
	}
}

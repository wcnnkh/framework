package io.basc.framework.log4j2.test;

import org.junit.Test;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.XUtils;

public class Log4j2Test {
	@Test
	public void test() {
		Logger logger =  LoggerFactory.getLogger(Log4j2Test.class.getName());
		//assertTrue(logger.isInfoEnabled());
		logger.info(XUtils.getUUID());
		logger.debug(XUtils.getUUID());
		logger.warn(XUtils.getUUID());
		logger.error(XUtils.getUUID());
		logger.trace(XUtils.getUUID());
	}
}

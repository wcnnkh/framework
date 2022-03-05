package io.basc.framework.log4j2.test;

import org.junit.Test;

import io.basc.framework.log4j2.Log4j2LoggerFactory;
import io.basc.framework.logger.Logger;
import io.basc.framework.util.XUtils;

public class Log4j2Test {
	@Test
	public void test() {
		Log4j2LoggerFactory loggerFactory = new Log4j2LoggerFactory();
		Logger logger =  loggerFactory.getLogger(Log4j2Test.class.getName());
		//assertTrue(logger.isInfoEnabled());
		logger.info(XUtils.getUUID());
	}
}

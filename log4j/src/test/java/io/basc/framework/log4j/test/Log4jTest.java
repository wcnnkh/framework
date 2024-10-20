package io.basc.framework.log4j.test;

import io.basc.framework.log4j.Log4jLoggerFactory;
import io.basc.framework.util.logging.LoggerFactory;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

import org.junit.Test;

public class Log4jTest {
	@Test
	public void def(){
		Logger logger = LogManager.getLogger(Log4jTest.class);
		logger.info("log4j def test");
	}
	
	
	@Test
	public void test(){
		LoggerFactory loggerFactory = new Log4jLoggerFactory();
		Logger logger = loggerFactory.getLogger(Log4jTest.class.getName());
		logger.info("log4j test");
	}
}

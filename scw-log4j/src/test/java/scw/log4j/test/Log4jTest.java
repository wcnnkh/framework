package scw.log4j.test;

import org.junit.Test;

import scw.log4j.Log4jLoggerFactory;
import scw.logger.ILoggerFactory;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class Log4jTest {
	@Test
	public void def(){
		Logger logger = LoggerFactory.getLogger(Log4jTest.class);
		logger.info("log4j def test");
	}
	
	
	@Test
	public void test(){
		ILoggerFactory loggerFactory = new Log4jLoggerFactory();
		Logger logger = loggerFactory.getLogger(Log4jTest.class.getName());
		logger.info("log4j test");
	}
}

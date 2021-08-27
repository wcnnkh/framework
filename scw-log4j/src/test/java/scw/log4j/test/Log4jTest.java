package scw.log4j.test;

import io.basc.framework.log4j.Log4jLoggerFactory;
import io.basc.framework.logger.ILoggerFactory;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

import org.junit.Test;

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

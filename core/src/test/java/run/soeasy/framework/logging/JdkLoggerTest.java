package run.soeasy.framework.logging;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JdkLoggerTest {
	@Test
	public void test() {
		JdkLoggerFactory loggerFactory = new JdkLoggerFactory();
		AbstractLogger logger = (AbstractLogger) loggerFactory.getLogger(JdkLoggerTest.class.getName());
		logger.setNeedToInferCaller(true);
		logger.info("AAA");
		assertTrue(logger.isInfoEnabled());
	}
}

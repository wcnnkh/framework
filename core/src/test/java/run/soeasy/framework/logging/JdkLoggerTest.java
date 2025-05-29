package run.soeasy.framework.logging;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JdkLoggerTest {
	@Test
	public void test() {
		JdkLoggerFactory loggerFactory = new JdkLoggerFactory();
		Logger logger = loggerFactory.getLogger(JdkLoggerTest.class.getName());
		logger.info("AAA");
		assertTrue(logger.isInfoEnabled());
	}
}

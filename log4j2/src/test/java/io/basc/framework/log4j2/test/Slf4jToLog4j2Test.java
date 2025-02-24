package io.basc.framework.log4j2.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.basc.framework.log4j2.Log4j2Utils;
import io.basc.framework.util.sequences.uuid.UUIDSequences;

public class Slf4jToLog4j2Test {
	@Test
	public void test() {
		Log4j2Utils.reconfigure();
		Logger logger = LoggerFactory.getLogger(Slf4jToLog4j2Test.class);
		// assertTrue(logger.isInfoEnabled());
		logger.info(UUIDSequences.global().next());
		logger.debug(UUIDSequences.global().next());
		logger.warn(UUIDSequences.global().next());
		logger.error(UUIDSequences.global().next());
		logger.trace(UUIDSequences.global().next());
	}
}

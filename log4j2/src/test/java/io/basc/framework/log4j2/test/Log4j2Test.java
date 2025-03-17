package io.basc.framework.log4j2.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import run.soeasy.framework.util.logging.CustomLevel;
import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;
import run.soeasy.framework.util.sequences.uuid.UUIDSequences;

public class Log4j2Test {
	@Test
	public void test() {
		LogManager.getConfigurable().setLevel("io.basc.framework.log4j2", CustomLevel.DEBUG);
		Logger logger = LogManager.getLogger(Log4j2Test.class.getName());
		// assertTrue(logger.isInfoEnabled());
		logger.info(UUIDSequences.global().next());
		logger.debug(UUIDSequences.global().next());
		logger.warn(UUIDSequences.global().next());
		logger.error(UUIDSequences.global().next());
		logger.trace(UUIDSequences.global().next());
		assertTrue(logger.isDebugEnabled());
	}
}

package io.basc.framework.slf4j;

import org.slf4j.LoggerFactory;

import io.basc.framework.util.logging.Logger;

public class Slf4jLoggerFactory implements LoggerFactory {

	@Override
	public Logger getLogger(String name) {
		org.slf4j.Logger logger = LoggerFactory.getLogger(name);
		return new Slf4jLogger(logger, null);
	}
}

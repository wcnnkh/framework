package run.soeasy.framework.slf4j;

import org.slf4j.LoggerFactory;

import run.soeasy.framework.util.logging.Logger;

public class Slf4jLoggerFactory implements run.soeasy.framework.util.logging.LoggerFactory {

	@Override
	public Logger getLogger(String name) {
		org.slf4j.Logger logger = LoggerFactory.getLogger(name);
		return new Slf4jLogger(logger, null);
	}
}

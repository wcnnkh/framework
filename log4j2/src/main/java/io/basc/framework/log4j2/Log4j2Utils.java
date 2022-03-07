package io.basc.framework.log4j2;

import java.io.IOException;

import org.apache.logging.log4j.core.config.Configurator;

import io.basc.framework.env.Environment;
import io.basc.framework.env.Sys;
import io.basc.framework.io.Resource;
import io.basc.framework.lang.NotSupportedException;

public class Log4j2Utils {
	private static final String DEFAULT_CONFIG_LOCATION = "io/basc/framework/log4j2/configuration.xml";
	private static final String CONFIG_LOCATION = "log4j2.xml";
	
	public static void defaultInit() {
		try {
			defaultInit(Sys.env);
		} catch (IOException e) {
			throw new NotSupportedException(e);
		}
	}

	public static void defaultInit(Environment environment) throws IOException {

		Resource resource = environment.getResource(CONFIG_LOCATION);
		if (resource.exists()) {
			init(environment, resource);
			return;
		}

		resource = environment.getResource(DEFAULT_CONFIG_LOCATION);
		init(environment, resource);
	}

	public static void init(Environment environment, String configLocation) throws IOException {
		init(environment, environment.getResource(configLocation));
	}

	public static void init(Environment environment, Resource resource) throws IOException {
		if (!resource.exists()) {
			return;
		}

		Configurator.reconfigure(resource.getURI());
	}
}

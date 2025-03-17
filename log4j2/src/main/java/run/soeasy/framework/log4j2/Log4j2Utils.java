package run.soeasy.framework.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import run.soeasy.framework.util.io.Resource;
import run.soeasy.framework.util.io.ResourceUtils;

public class Log4j2Utils {
	private static Logger logger = LogManager.getLogger(Log4j2Utils.class);
	private static final String DEFAULT_CONFIG_LOCATION = "io/basc/framework/log4j2/configuration.xml";
	private static final String CONFIG_LOCATION = "log4j2.xml";

	public static void reconfigure() {
		Resource resource = ResourceUtils.getSystemResource(CONFIG_LOCATION);
		if (resource != null && resource.exists()) {
			reconfigure(resource);
			return;
		}

		resource = ResourceUtils.getSystemResource(DEFAULT_CONFIG_LOCATION);
		reconfigure(resource);
	}

	public static void reconfigure(Resource resource) {
		if (resource == null || !resource.exists()) {
			return;
		}

		try {
			Configurator.reconfigure(resource.getURI());
		} catch (Throwable e) {
			logger.error(resource.getDescription(), e);
		}
	}
}

package io.basc.framework.context.resource;

import io.basc.framework.context.config.ConfigurableApplicationContextSourceProcessor;
import io.basc.framework.io.Resource;
import io.basc.framework.util.Elements;

public class ConfigurableApplicationContextResourceImporter extends
		ConfigurableApplicationContextSourceProcessor<Elements<? extends Resource>, ApplicationContextResourceImporter, ApplicationContextResourceImportExtender>
		implements ApplicationContextResourceImporter, ApplicationContextResourceImportExtender {

	public ConfigurableApplicationContextResourceImporter() {
		setServiceClass(ApplicationContextResourceImporter.class);
		getExtender().setServiceClass(ApplicationContextResourceImportExtender.class);
	}
}

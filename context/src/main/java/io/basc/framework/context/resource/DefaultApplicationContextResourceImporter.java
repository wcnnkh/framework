package io.basc.framework.context.resource;

import io.basc.framework.context.config.ApplicationContextSourceProcessor;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.io.Resource;
import lombok.Getter;

@Getter
public class DefaultApplicationContextResourceImporter extends ConfigurableApplicationContextResourceImporter {
	private final ApplicationContextPropertiesResourceImporter propertiesResourceImporter = new ApplicationContextPropertiesResourceImporter();

	public DefaultApplicationContextResourceImporter() {
		doNativeConfigure();
		getExtender().doNativeConfigure();
	}

	@Override
	public void process(ConfigurableApplicationContext context, Elements<? extends Resource> source,
			ApplicationContextSourceProcessor<? super Elements<? extends Resource>> chain) {
		super.process(context, source, chain);
	}
}

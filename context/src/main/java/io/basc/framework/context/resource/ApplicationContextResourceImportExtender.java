package io.basc.framework.context.resource;

import io.basc.framework.context.config.ApplicationContextSourceProcessExtender;
import io.basc.framework.context.config.ApplicationContextSourceProcessor;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.io.Resource;

public interface ApplicationContextResourceImportExtender
		extends ApplicationContextSourceProcessExtender<Elements<? extends Resource>> {
	@Override
	void process(ConfigurableApplicationContext context, Elements<? extends Resource> source,
			ApplicationContextSourceProcessor<? super Elements<? extends Resource>> chain);
}

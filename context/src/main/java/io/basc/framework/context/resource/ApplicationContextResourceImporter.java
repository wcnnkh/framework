package io.basc.framework.context.resource;

import io.basc.framework.context.config.ApplicationContextSourceProcessor;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.util.Elements;
import io.basc.framework.util.io.Resource;

/**
 * 资源导入
 * 
 * @author shuchaowen
 *
 */
public interface ApplicationContextResourceImporter
		extends ApplicationContextSourceProcessor<Elements<? extends Resource>> {
	@Override
	void process(ConfigurableApplicationContext context, Elements<? extends Resource> source);
}

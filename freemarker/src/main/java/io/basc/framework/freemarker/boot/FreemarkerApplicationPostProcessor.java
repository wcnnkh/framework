package io.basc.framework.freemarker.boot;

import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Component;
import io.basc.framework.freemarker.FreemarkerUtils;

@Component
class FreemarkerApplicationPostProcessor implements ApplicationPostProcessor {

	@Override
	public void postProcessApplication(ConfigurableApplication application) {
		FreemarkerUtils.ensureLoggerLibrary();
	}

}

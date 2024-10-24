package io.basc.framework.freemarker.boot;

import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.freemarker.FreemarkerUtils;

@Provider(order = Ordered.HIGHEST_PRECEDENCE)
public class FreemarkerApplicationPostProcessor implements ApplicationPostProcessor {

	@Override
	public void postProcessApplication(ConfigurableApplication application) {
		FreemarkerUtils.ensureLoggerLibrary();
	}

}

package io.basc.framework.dubbo.boot;

import org.apache.dubbo.config.ServiceConfig;

import io.basc.framework.boot.ApplicationPostProcessor;
import io.basc.framework.boot.ConfigurableApplication;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.dubbo.DubboServiceRegistry;

@ConditionalOnParameters
public class DubboApplicationPostProcessor implements ApplicationPostProcessor {

	@Override
	public void postProcessApplication(ConfigurableApplication application) throws Throwable {
		boolean enable = true;
		for (Class<?> clazz : application.getSourceClasses()) {
			DubboEnable dubboEnable = clazz.getAnnotation(DubboEnable.class);
			if (dubboEnable != null && !dubboEnable.value()) {
				enable = false;
			}
		}

		if (enable && application.isInstance(DubboServiceRegistry.class)) {
			DubboServiceRegistry registry = application.getInstance(DubboServiceRegistry.class);
			for (ServiceConfig<?> serviceConfig : registry.getServices()) {
				serviceConfig.export();
			}
		}
	}

}

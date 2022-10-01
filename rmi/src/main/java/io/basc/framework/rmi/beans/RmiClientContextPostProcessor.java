package io.basc.framework.rmi.beans;

import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.util.StringUtils;

@Provider
public class RmiClientContextPostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) {
		for (Class<?> clazz : context.getContextClasses()) {
			RmiClient client = clazz.getAnnotation(RmiClient.class);
			if (client == null) {
				continue;
			}

			if (!context.containsDefinition(clazz.getName())) {
				String host = StringUtils.IS_EMPTY.negate().first(client.host(), client.value());
				context.registerDefinition(new RmiClientBeanDefinition(context, clazz, host));
			}
		}
	}

}
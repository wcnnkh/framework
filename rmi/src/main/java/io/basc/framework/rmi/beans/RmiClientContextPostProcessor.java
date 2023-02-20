package io.basc.framework.rmi.beans;

import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.util.Cursor;
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
				String host = Cursor.of(client.host(), client.value()).filter((e) -> StringUtils.isNotEmpty(e)).first();
				context.registerDefinition(new RmiClientBeanDefinition(context, clazz, host));
			}
		}
	}

}

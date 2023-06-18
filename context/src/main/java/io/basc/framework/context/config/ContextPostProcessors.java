package io.basc.framework.context.config;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.context.ContextException;

public final class ContextPostProcessors extends ConfigurableServices<ContextPostProcessor>
		implements ContextPostProcessor {

	public ContextPostProcessors() {
		super(ContextPostProcessor.class);
	}

	@Override
	public void postProcessContext(ConfigurableContext context) {
		for (ContextPostProcessor postProcessor : getServices()) {
			try {
				postProcessor.postProcessContext(context);
			} catch (Throwable e) {
				throw new ContextException("Post process context[" + postProcessor + "]", e);
			}
		}
	}

}

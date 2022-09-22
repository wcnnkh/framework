package io.basc.framework.context.support;

import io.basc.framework.context.Context;
import io.basc.framework.context.ContextResolver;
import io.basc.framework.env.Environment;
import io.basc.framework.orm.support.Configurator;

public class ContextConfigurator extends Configurator {

	public ContextConfigurator(Context context) {
		this(context, context.getContextResolver());
	}

	public ContextConfigurator(Environment environment, ContextResolver contextResolver) {
		super(environment);
		getContext().addFilter((field) -> {
			if (field.isSupportSetter() && contextResolver.hasContext(field.getSetter())) {
				return false;
			}
			return true;
		});
	}
}

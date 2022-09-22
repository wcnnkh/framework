package io.basc.framework.context.support;

import io.basc.framework.context.Context;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.EnvironmentBeanDefinition;

public class ContextBeanDefinition extends EnvironmentBeanDefinition{
	private final Context context;
	
	public ContextBeanDefinition(Context context, Class<?> type) {
		this(context, TypeDescriptor.valueOf(type));
	}

	public ContextBeanDefinition(Context context, TypeDescriptor typeDescriptor) {
		super(context, typeDescriptor);
		this.context = context;
	}
	
	public Context getContext() {
		return context;
	}
}

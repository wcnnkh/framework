package io.basc.framework.context.ioc.support;

import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.ConfigurableIocResolver;
import io.basc.framework.context.ioc.annotation.IocBeanResolverExtend;

public class DefaultIocResolver extends ConfigurableIocResolver {

	public DefaultIocResolver(Context context) {
		addService(new IocBeanResolverExtend(context));
	}
}

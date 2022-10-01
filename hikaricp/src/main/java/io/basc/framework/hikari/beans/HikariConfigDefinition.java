package io.basc.framework.hikari.beans;

import com.zaxxer.hikari.HikariConfig;

import io.basc.framework.context.Context;
import io.basc.framework.context.support.ContextBeanDefinition;
import io.basc.framework.context.support.ContextConfigurator;
import io.basc.framework.db.Configurable;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.hikari.HikariUtils;

public class HikariConfigDefinition extends ContextBeanDefinition {

	public HikariConfigDefinition(Context context) {
		super(context, HikariConfig.class);
	}

	@Override
	public boolean isInstance(Class<?>[] parameterTypes) {
		return getBeanFactory().isInstance(Configurable.class);
	}

	@Override
	public Object create() throws InstanceException {
		Configurable configurable = getBeanFactory().getInstance(Configurable.class);
		HikariConfig hikariConfig = new HikariConfig();
		HikariUtils.config(hikariConfig, configurable);
		ContextConfigurator configurator = new ContextConfigurator(getContext());
		configurator.getContext().setNamePrefix("hikari.");
		configurator.transform(hikariConfig);
		return hikariConfig;
	}
}

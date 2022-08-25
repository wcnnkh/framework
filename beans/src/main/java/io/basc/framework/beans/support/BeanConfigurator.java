package io.basc.framework.beans.support;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.env.Environment;
import io.basc.framework.orm.support.Configurator;

public class BeanConfigurator extends Configurator {
	private static final String IGNORE_PACKAGE_NAME_PREFIX = BeanFactory.class.getPackage().getName() + ".**";

	public BeanConfigurator(Environment environment) {
		super(environment);
		getContext().getIgnoreAnnotationNameMatcher().register(IGNORE_PACKAGE_NAME_PREFIX, false);
	}

	public Environment getEnvironment() {
		return (Environment) source;
	}
}

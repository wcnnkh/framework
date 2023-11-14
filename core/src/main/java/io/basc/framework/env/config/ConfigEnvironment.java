package io.basc.framework.env.config;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.support.DefaultBeanFactory;

public class ConfigEnvironment extends DefaultBeanFactory{
	private ConfigEnvironment parentEnvironment;
	private 

	public ConfigEnvironment(Scope scope) {
		super(scope);
	}
}

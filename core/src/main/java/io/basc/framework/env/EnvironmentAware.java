package io.basc.framework.env;

import io.basc.framework.factory.support.DefaultInstanceDefinition;

/**
 * 工厂模式应该实现此注入
 * 
 * @author shuchaowen
 * @see DefaultInstanceDefinition
 */
public interface EnvironmentAware {
	void setEnvironment(Environment environment);
}

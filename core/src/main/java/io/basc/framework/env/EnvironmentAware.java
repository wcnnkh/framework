package io.basc.framework.env;

/**
 * 工厂模式应该实现此注入
 * 
 * @author shuchaowen
 */
public interface EnvironmentAware {
	void setEnvironment(Environment environment);
}

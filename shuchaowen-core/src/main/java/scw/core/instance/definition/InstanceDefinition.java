package scw.core.instance.definition;

import java.lang.reflect.AnnotatedElement;

import scw.core.instance.InstanceBuilder;

public interface InstanceDefinition extends InstanceBuilder<Object> {
	String getId();

	Class<?> getTargetClass();

	boolean isSingleton();

	/**
	 * 是否可以调用create()方法实例化
	 * 
	 * @return
	 */
	boolean isInstance();

	AnnotatedElement getAnnotatedElement();

	void init(Object instance) throws Exception;
}

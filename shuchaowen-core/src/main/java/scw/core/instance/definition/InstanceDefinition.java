package scw.core.instance.definition;

import java.lang.reflect.AnnotatedElement;

public interface InstanceDefinition {
	String getId();
	
	Class<?> getTargetClass();
	
	boolean isSingleton();
	
	/**
	 * 是否可以调用create()方法实例化
	 * 
	 * @return
	 */
	boolean isInstance();

	<T> T create() throws Exception;

	<T> T create(Object... params) throws Exception;

	<T> T create(Class<?>[] parameterTypes, Object... params) throws Exception;
	
	AnnotatedElement getAnnotatedElement();
}
